package com.lindzh.hasting.cluster.zk;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.lindzh.hasting.cluster.hash.Hashing;
import com.lindzh.hasting.cluster.hash.RoundRobinHashing;
import com.lindzh.hasting.cluster.limit.LimitDefine;
import com.lindzh.hasting.rpc.cluster1.AbstractRpcClusterClientExecutor;
import com.lindzh.hasting.rpc.cluster1.ConsumeRpcObject;
import com.lindzh.hasting.rpc.cluster1.HostWeight;
import com.lindzh.hasting.rpc.cluster1.RpcHostAndPort;
import com.lindzh.hasting.cluster.JSONUtils;
import com.lindzh.hasting.cluster.MD5Utils;
import com.lindzh.hasting.cluster.RpcClusterUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;

import com.lindzh.hasting.rpc.RpcService;
import com.lindzh.hasting.rpc.exception.RpcException;
import com.lindzh.hasting.rpc.net.RpcNetBase;
import org.apache.zookeeper.Watcher;

public class ZkRpcClientExecutor extends AbstractRpcClusterClientExecutor {
	
	private CuratorFramework zkclient;

	private String namespace = "rpc";
	
	private String connectString;
	
	private int connectTimeout = 8000;
	
	private int maxRetry = 5;
	
	private int baseSleepTime = 1000;
	
	private String defaultEncoding = "utf-8";

	private boolean isAdmin = false;

	private Map<String,List<HostWeight>> applicationWeightMap = new HashMap<String,List<HostWeight>>();

	private List<RpcHostAndPort> rpcServersCache = new CopyOnWriteArrayList<RpcHostAndPort>();

	private Map<String, List<RpcService>> rpcServiceCache = new ConcurrentHashMap<String, List<RpcService>>();

	private HashSet<ConsumeRpcObject> consumeServices = new HashSet<ConsumeRpcObject>();

	private Hashing hashing = new RoundRobinHashing();

	private Logger logger = Logger.getLogger("rpcCluster");

	private Random random = new Random();
	
	//add timer to execute fetch all task
	private Timer timer = new Timer();
	
	private long taskDelay = 10000;
	
	private CuratorWatcher providerWatcher = new CuratorWatcher(){
		@Override
		public void process(WatchedEvent event) throws Exception {
			ZkRpcClientExecutor.this.fetchRpcServers(true);
		}
	};
	
	private String genServerListKey() {
		return "/servers";
	}

	private String getServiceListKey(String serverKey) {
		return "/services/" + serverKey;
	}
	
	private String genServerKey(RpcHostAndPort hostAndPort) {
		return MD5Utils.hostMd5(hostAndPort.getApplication(),hostAndPort.getHost(),hostAndPort.getPort());
	}
	
	private void initZk(){
		ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(baseSleepTime, maxRetry);
		zkclient = CuratorFrameworkFactory.builder().namespace(namespace).connectString(connectString)
		.connectionTimeoutMs(connectTimeout).sessionTimeoutMs(connectTimeout).retryPolicy(retryPolicy).build();
		zkclient.start();
	}
	
	private void handleZkException(Exception e){
		logger.error("zk error", e);
		throw new RpcException(e);
	}
	
	/**
	 * 新的server添加进来
	 * @param serverMd5
	 * @return
	 */
	private boolean isNew(String serverMd5){
		boolean isNew = true;
		for(RpcHostAndPort hap:rpcServersCache){
			String oldMd5 = MD5Utils.hostMd5(hap);
			if(oldMd5.equals(serverMd5)){
				isNew = false;
				break;
			}
		}
		return isNew;
	}
	
	/**
	 * 旧的server去掉
	 * @param pathes
	 * @param hap
	 * @return
	 */
	private boolean isOld(List<String> pathes,RpcHostAndPort hap){
		boolean isOld = true;
		String oldMd5 = MD5Utils.hostMd5(hap);
		for(String path:pathes){
			if(path.contains(oldMd5)){
				isOld = false;
			}
		}
		if(isOld){
			logger.info("[OLD] md5:"+oldMd5);
		}
		return isOld;
	}
	
	private void getServices(List<String> services,RpcHostAndPort hostAndPort){
		String hostAndPortString = hostAndPort.toString();
		String serverKey = this.genServerKey(hostAndPort);
		String serviceListKey = this.getServiceListKey(serverKey);
		List<RpcService> rpcServices = new CopyOnWriteArrayList<RpcService>();
		for(String service:services){
			String key = serviceListKey+"/"+service;
			try{
				byte[] data = this.zkclient.getData().forPath(key);
				String json = new String(data,this.defaultEncoding);
				RpcService rpcService = JSONUtils.fromJSON(json, RpcService.class);
				rpcServices.add(rpcService);
			}catch(Exception e){
				this.handleZkException(e);
			}
		}
		logger.info("[SERVICES] host:"+hostAndPortString+" services:"+ JSONUtils.toJSON(rpcServices));
		rpcServiceCache.put(hostAndPortString, rpcServices);
	}
	
	private void fetchServices(RpcHostAndPort hostAndPort){
		String serverKey = this.genServerKey(hostAndPort);
		String serviceListKey = this.getServiceListKey(serverKey);
		try {
			List<String> services = this.zkclient.getChildren().forPath(serviceListKey);
			this.getServices(services, hostAndPort);
		} catch (Exception e) {
			if(e instanceof KeeperException.NoNodeException){
				return;
			}
			this.handleZkException(e);
		}
	}
	
	private void addServer(RpcHostAndPort hostAndPort,boolean startConnections){
		boolean add = true;
		String server = hostAndPort.toString();
		synchronized (rpcServersCache) {
			for(RpcHostAndPort hap:rpcServersCache){
				if(hap.toString().equals(server)){
					add = false;
					break;
				}
			}
			if(add){
				rpcServersCache.add(hostAndPort);
			}
		}
		//获取服务列表
		if(add){
			this.fetchServices(hostAndPort);
		}
		//添加并启动链接
		if(add&&startConnections){
			this.startConnector(hostAndPort);
		}
	}
	
	private void removeServer0(RpcHostAndPort hostAndPort){
		String server = hostAndPort.toString();
		logger.info("removeServer " + server);
		this.rpcServiceCache.remove(server);
		super.removeServer(hostAndPort.toString());
		List<RpcHostAndPort> hostAndPorts = new CopyOnWriteArrayList<RpcHostAndPort>();
		for (RpcHostAndPort hap : rpcServersCache) {
			if (!hap.toString().equals(server)) {
				hostAndPorts.add(hap);
			}
		}
		synchronized (this) {
			rpcServersCache = hostAndPorts;
		}
	}
	
	private void fetchServers(List<String> pathes,boolean startConnections){
		//watch for change
		try{
			this.zkclient.getChildren().usingWatcher(providerWatcher).inBackground().forPath(this.genServerListKey());
		}catch(Exception e){
			this.handleZkException(e);
		}
		if(pathes!=null){
			for(String path:pathes){
				if(this.isNew(path)){
					String key = this.genServerListKey()+"/"+path;
					try {
						byte[] data = this.zkclient.getData().forPath(key);
						String json = new String(data,this.defaultEncoding);
						RpcHostAndPort hostAndPort = JSONUtils.fromJSON(json, RpcHostAndPort.class);
						this.addServer(hostAndPort, startConnections);
					} catch (Exception e) {
						this.handleZkException(e);
					}
				}
			}
			for(RpcHostAndPort hostAndPort:rpcServersCache){
				if(this.isOld(pathes, hostAndPort)){
					logger.info("[OLD REMOVE] "+ JSONUtils.toJSON(hostAndPort)+" pathes:"+ JSONUtils.toJSON(pathes));
					this.removeServer0(hostAndPort);
				}
			}
		}
	}
	
	private void fetchRpcServers(boolean startConnectors) {
		try{
			List<String> pathes = this.zkclient.getChildren().forPath(this.genServerListKey());
			this.fetchServers(pathes,startConnectors);
		}catch(Exception e){
			this.handleZkException(e);
		}
	}

	@Override
	public void onStart(RpcNetBase network) {
		
	}

	@Override
	public List<RpcHostAndPort> getHostAndPorts() {
		return rpcServersCache;
	}

	@Override
	public List<RpcService> getServerService(RpcHostAndPort hostAndPort) {
		if (hostAndPort != null) {
			String key = hostAndPort.toString();
			logger.info("[SERVICES] getservices:"+hostAndPort+" services:"+ JSONUtils.toJSON(rpcServiceCache.get(key)));
			return rpcServiceCache.get(key);
		}
		return Collections.emptyList();
	}

	@Override
	public void startRpcCluster() {
		//启动zk
		this.initZk();
		//获取机器和服务列表
		this.fetchRpcServers(false);

		if(!isAdmin){
			//上报消费者信息
			RpcHostAndPort rpcHostAndPort = RpcClusterUtils.genConsumerInfo(this.getApplication(), this.getSelfIp());
			this.doUploadServerInfo(rpcHostAndPort);
			//上报消费
			this.doUpload();
		}
	}

	@Override
	public void stopRpcCluster() {
		this.zkclient.close();
		rpcServersCache = null;
		logger.info("[SERVER] stop rpc server clear services");
		rpcServiceCache.clear();
		timer.cancel();
	}

	@Override
	public String hash(List<RpcHostAndPort> servers) {
		return this.hashing.hash(servers);
	}

	@Override
	public void onClose(RpcHostAndPort hostAndPort) {
		this.closeServer(hostAndPort);
		//可能是暂时的网络抖动引起的，因此需要再次获取
		timer.schedule(new TimerTask() {
			public void run() {
				ZkRpcClientExecutor.this.fetchRpcServers(true);
			}
		}, this.taskDelay);
	}
	
	private void closeServer(RpcHostAndPort hostAndPort) {
		logger.info("[SERVER] close server "+hostAndPort);
		rpcServiceCache.remove(hostAndPort.toString());
		this.removeServer0(hostAndPort);
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getConnectString() {
		return connectString;
	}

	public void setConnectString(String connectString) {
		this.connectString = connectString;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public int getMaxRetry() {
		return maxRetry;
	}

	public void setMaxRetry(int maxRetry) {
		this.maxRetry = maxRetry;
	}

	public int getBaseSleepTime() {
		return baseSleepTime;
	}

	public void setBaseSleepTime(int baseSleepTime) {
		this.baseSleepTime = baseSleepTime;
	}

	@Override
	public <T> void doRegisterRemote(String application,Class<T> iface, String version, String group) {
		String ip = this.getSelfIp();
		ConsumeRpcObject consumeObject = new ConsumeRpcObject();
		consumeObject.setApplication(application);
		consumeObject.setGroup(group);
		consumeObject.setClassName(iface.getName());
		consumeObject.setVersion(version);
		consumeObject.setIp(ip);
		consumeServices.add(consumeObject);

		if(zkclient!=null){
			this.doUpload(consumeObject);
		}
	}

	public List<String> getConsumeApplications(String group,String service,String version){
		String serviceDirName = group+"_"+service+"_"+version;
		String consumerBase = "/consumers/"+serviceDirName;
		try {
			return zkclient.getChildren().forPath(consumerBase);
		} catch (Exception e) {
			if(e instanceof KeeperException.NoNodeException){
				return Collections.emptyList();
			}
			throw new RpcException(e);
		}
	}

	public List<ConsumeRpcObject> getConsumeObjects(String group,String service,String version){
		List<ConsumeRpcObject> consumers = new ArrayList<ConsumeRpcObject>();
		String serviceDirName = group+"_"+service+"_"+version;
		String consumerBase = "/consumers/"+serviceDirName;
		List<String> apps = this.getConsumeApplications(group, service, version);
		for(String app:apps){
			String hostipDir = consumerBase+"/"+app;
			try {
				List<String> hosts = zkclient.getChildren().forPath(hostipDir);
				for(String host:hosts){
					ConsumeRpcObject con = new ConsumeRpcObject();
					con.setIp(host);
					con.setVersion(version);
					con.setClassName(service);
					con.setGroup(group);
					con.setApplication(app);
					consumers.add(con);
				}
			} catch (Exception e) {
				if(e instanceof KeeperException.NoNodeException){
					continue;
				}
				throw new RpcException(e);
			}
		}
		return consumers;
	}

	private void doUpload(ConsumeRpcObject rpc){
		//group service version
		String service = rpc.getGroup()+"_"+rpc.getClassName()+"_"+rpc.getVersion();
		try {
			//当前机器临时节点
			String host = "/consumers/"+service+"/"+rpc.getApplication()+"/"+rpc.getIp();
			byte[] data = JSONUtils.toJSON(rpc).getBytes();
			zkclient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(host,data);
		} catch (Exception e) {
			if(e instanceof KeeperException.NodeExistsException){

			}else{
				throw new RpcException(e);
			}
		}
	}

	/**
	 * 消费者信息上传到注册中心
	 */
	private void doUpload(){
		if(consumeServices.size()>0){
			for(ConsumeRpcObject rpc:consumeServices){
				this.doUpload(rpc);
			}
		}
	}

	private void watchWeight(final String application){

		String applicationWeightsKey = ZKUtils.genApplicationWeightsKey(application);
		try{
			zkclient.getData().usingWatcher(new CuratorWatcher() {
				@Override
				public void process(WatchedEvent watchedEvent) throws Exception {
					if(watchedEvent.getType()== Watcher.Event.EventType.NodeDataChanged){
						//拿到权重列表
						doGetWeights(application,true);
					}
				}
			}).inBackground().forPath(applicationWeightsKey);
		}catch(Exception e){
			logger.error("[zookeeper] watch "+applicationWeightsKey,e);
		}
	}

	@Override
	public List<HostWeight> getWeights(String application) {
		return this.doGetWeights(application,false);
	}

	/**
	 * 获取权重列表
	 * @param application
	 * @return
	 * 从缓存获取
     */
	public List<HostWeight> doGetWeights(String application,boolean fromRegister) {
		if(!fromRegister){
			List<HostWeight> weights = applicationWeightMap.get(application);
			if(weights!=null){
				return weights;
			}
		}

		String weightsKey =  ZKUtils.genApplicationWeightsKey(application);
		ArrayList<HostWeight> result = new ArrayList<HostWeight>();
		try {
			List<String> hosts = zkclient.getChildren().forPath(weightsKey);
			if(hosts!=null&&hosts.size()>0){
				for(String host:hosts){
					String genWeightKey = ZKUtils.genWeightKey(application,host);
					byte[] data = zkclient.getData().forPath(genWeightKey);
					if(data!=null){
						String ww = new String(data);
						int w = Integer.parseInt(ww);
						String[] hostport = host.split(":");
						HostWeight weight = new HostWeight();
						weight.setWeight(w);
						weight.setHost(hostport[0]);
						weight.setPort(Integer.parseInt(hostport[1]));
						result.add(weight);
					}
				}
			}

		} catch (Exception e) {
			throw new RpcException(e);
		}
		//监控数据变化
		this.watchWeight(application);

		applicationWeightMap.put(application,result);
		return result;
	}

	@Override
	public void setWeight(String application, HostWeight weight) {
		ZKUtils.doSetWehgit(zkclient,application,weight.getKey(),weight.getWeight(),true);
	}

	private String genServerKey(String host,int port) {
		return ZKUtils.genServerKey(MD5Utils.hostMd5(this.getApplication(),host,port));
	}

	/**
	 * 更改权重
	 * @param application
	 * @param limits
     */
	void setLimits(String application,List<LimitDefine> limits){
		String key = ZKUtils.genLimitKey(application);
		String data = JSONUtils.toJSON(limits);
		try{
			zkclient.setData().forPath(key,data.getBytes());
			logger.error("[ZK] set limit success key:"+key+" data:"+data);
		}catch (Exception e){
			if(e instanceof KeeperException.NoNodeException){
				try {
					zkclient.create().forPath(key,data.getBytes());
				} catch (Exception e1) {
					logger.error("[ZK] set limit error key:"+key+" data:"+data);
					throw new RpcException(e1);
				}
			}else{
				logger.error("[ZK] set limit error key:"+key+" data:"+data);
				throw new RpcException(e);
			}
		}
	}

	private void doUploadServerInfo(RpcHostAndPort hostAndPort){
		String serverKey = this.genServerKey(hostAndPort.getHost(),hostAndPort.getPort());
		String hostAndPortJson = JSONUtils.toJSON(hostAndPort);
		logger.info("create rpc provider:"+hostAndPortJson);
		try{
			byte[] data = hostAndPortJson.getBytes(defaultEncoding);
			this.zkclient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(serverKey, data);
			logger.info("add rpc provider success "+serverKey);
		}catch(Exception e){
			if(e instanceof KeeperException.NodeExistsException){
				return;
			}
			logger.error("add provider error",e);
			throw new RpcException(e);
		}
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean admin) {
		isAdmin = admin;
	}

}
