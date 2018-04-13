package com.lindzh.hasting.cluster.etcd;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.lindzh.hasting.cluster.hash.Hashing;
import com.lindzh.hasting.cluster.hash.RoundRobinHashing;
import com.lindzh.hasting.cluster.limit.LimitDefine;
import com.lindzh.hasting.rpc.cluster1.AbstractRpcClusterClientExecutor;
import com.lindzh.hasting.rpc.cluster1.ConsumeRpcObject;
import com.lindzh.hasting.rpc.cluster1.HostWeight;
import com.lindzh.hasting.rpc.cluster1.RpcHostAndPort;
import com.lindzh.hasting.rpc.exception.RpcException;
import com.lindzh.hasting.cluster.JSONUtils;
import com.lindzh.hasting.cluster.MD5Utils;
import com.lindzh.hasting.cluster.RpcClusterUtils;
import org.apache.log4j.Logger;

import com.lindzh.hasting.rpc.RpcService;
import com.lindzh.hasting.rpc.net.RpcNetBase;
import com.lindzh.jetcd.EtcdChangeResult;
import com.lindzh.jetcd.EtcdClient;
import com.lindzh.jetcd.EtcdNode;
import com.lindzh.jetcd.EtcdResult;
import com.lindzh.jetcd.EtcdWatchCallback;

public class EtcdRpcClientExecutor extends AbstractRpcClusterClientExecutor {

	private EtcdClient etcdClient;

	private String namespace = "rpc";

	private String etcdUrl;

	private List<RpcHostAndPort> rpcServersCache = new ArrayList<RpcHostAndPort>();

	private Map<String, List<RpcService>> rpcServiceCache = new ConcurrentHashMap<String, List<RpcService>>();

	private Map<String,List<HostWeight>> applicationWeightMap = new HashMap<String,List<HostWeight>>();

	private Hashing hashing = new RoundRobinHashing();

	private Logger logger = Logger.getLogger("rpcCluster");

	private HashSet<ConsumeRpcObject> consumeServices = new HashSet<ConsumeRpcObject>();

	private int ttl = 60;

	private Timer timer = new Timer();

	private Random random = new Random();

	private RpcHostAndPort selfHost;

	private boolean isAdmin = false;

	private EtcdWatchCallback etcdServerWatcher = new EtcdWatchCallback() {
		public void onChange(EtcdChangeResult future) {
			logger.info("servers change");
			EtcdRpcClientExecutor.this.fetchRpcServers(true);
		}
	};

	private EtcdWatchCallback etcdServicesWatcher = new EtcdWatchCallback() {
		public void onChange(EtcdChangeResult future) {
			logger.info("serviceChange");
			EtcdResult result = future.getResult();
			if (result != null && result.isSuccess()) {
				String key = result.getNode().getKey();
				RpcHostAndPort hostAndPort = EtcdRpcClientExecutor.this.getServerAndHost(key);
				if (hostAndPort != null) {
					EtcdRpcClientExecutor.this.fetchRpcServices(hostAndPort);
				}
			}
		}
	};

	private RpcHostAndPort getServerAndHost(String key) {
		for (RpcHostAndPort hostAndPort : rpcServersCache) {
			String serverKey = this.genServerKey(hostAndPort);
			if (key.contains(serverKey)) {
				return hostAndPort;
			}
		}
		return null;
	}

	private String genServerListKey() {
		return "/" + namespace + "/servers";
	}

	private String getServiceListKey(String serverKey) {
		return "/" + namespace + "/services/" + serverKey;
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
			return rpcServiceCache.get(key);
		}
		return Collections.emptyList();
	}

	@Override
	public void startRpcCluster() {
		this.etcdClient = new EtcdClient(etcdUrl);
		etcdClient.start();
		this.fetchRpcServers(false);
		//上报消费者信息
		selfHost = RpcClusterUtils.genConsumerInfo(this.getApplication(), this.getSelfIp());

		if(!this.isAdmin){
			this.doUploadServerInfo();
			this.doUpload();
		}
	}

	private void startHeartBeat(){
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				EtcdRpcClientExecutor.this.doUpload();
			}
		},new Date(),(ttl/3)*1000);
	}

	private void doUploadServerInfo(){
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				String key = "/"+namespace+"/servers/" + MD5Utils.hostMd5(selfHost);
				String value = JSONUtils.toJSON(selfHost);
				etcdClient.set(key,value,ttl);
			}
		},new Date(),(ttl/3)*1000);
	}

	@Override
	public void stopRpcCluster() {
		rpcServersCache = null;
		etcdClient.stop();
		rpcServiceCache.clear();
		timer.cancel();
	}

	@Override
	public String hash(List<RpcHostAndPort> servers) {
		return hashing.hash(servers);
	}

	@Override
	public void onClose(RpcHostAndPort hostAndPort) {
		this.closeServer(hostAndPort);
	}

	private void closeServer(RpcHostAndPort hostAndPort) {
		rpcServiceCache.remove(hostAndPort.toString());
		this.removeServer2(hostAndPort.toString());
	}

	private void removeServer2(String server) {
		logger.info("removeServer " + server);
		super.removeServer(server);
		List<RpcHostAndPort> hostAndPorts = new ArrayList<RpcHostAndPort>();
		for (RpcHostAndPort hap : rpcServersCache) {
			if (!hap.toString().equals(server)) {
				hostAndPorts.add(hap);
			}
		}
		synchronized (this) {
			rpcServersCache = hostAndPorts;
		}
	}

	private void updateServerNodes(List<EtcdNode> nodes,boolean startConnector) {
		if (nodes != null) {
			// 获取新的列表和老的列表对比
			HashSet<String> newServers = new HashSet<String>();
			HashSet<String> needAdd = new HashSet<String>();
			HashMap<String, RpcHostAndPort> newServerMap = new HashMap<String, RpcHostAndPort>();
			for (EtcdNode node : nodes) {
				String value = node.getValue();
				RpcHostAndPort hostAndPort = JSONUtils.fromJSON(value,RpcHostAndPort.class);
				String key = hostAndPort.toString();
				newServerMap.put(key, hostAndPort);
				newServers.add(key);
				needAdd.add(key);
			}

			// 移除的server节点
			Set<String> oldServers = RpcClusterUtils.toString(rpcServersCache);
			needAdd.removeAll(oldServers);
			oldServers.removeAll(newServers);
			for (String old : oldServers) {
				this.removeServer2(old);
			}

			logger.info("needAddServer:"+JSONUtils.toJSON(needAdd));
			// 新增加的server节点
			for (String server : needAdd) {
				RpcHostAndPort hostAndPort = newServerMap.get(server);
				rpcServersCache.add(hostAndPort);
				this.fetchRpcServices(hostAndPort);
				if(startConnector){
					this.startConnector(hostAndPort);
				}
			}
		}
	}

	private void fetchRpcServers(boolean startConnectors) {
		EtcdResult result = etcdClient.children(this.genServerListKey(), true,true);
		if (result.isSuccess()) {
			logger.info("rpcServers:"+JSONUtils.toJSON(result));
			EtcdNode node = result.getNode();
			List<EtcdNode> nodes = node.getNodes();
			this.updateServerNodes(nodes,startConnectors);
			// 监控节点数据变化
			this.etcdClient.watchChildren(this.genServerListKey(), true, true,etcdServerWatcher);
		} else {

		}
	}

	private String genServerKey(RpcHostAndPort hostAndPort) {
		return MD5Utils.hostMd5(hostAndPort);
	}

	/**
	 * 获取server提供的服务列表
	 * 
	 * @param hostAndPort
	 */
	private void fetchRpcServices(final RpcHostAndPort hostAndPort) {
		String hostAndPortString = hostAndPort.toString();
		String serverKey = this.genServerKey(hostAndPort);
		String serviceListKey = this.getServiceListKey(serverKey);
		EtcdResult result = this.etcdClient.children(serviceListKey, true, true);
		if (result.isSuccess()) {
			logger.info("server:"+hostAndPortString+" services:"+JSONUtils.toJSON(result));
			List<EtcdNode> nodes = result.getNode().getNodes();
			if (nodes != null) {
				ArrayList<RpcService> services = new ArrayList<RpcService>();
				for (EtcdNode node : nodes) {
					String rpcJson = node.getValue();
					RpcService rpcService = JSONUtils.fromJSON(rpcJson,RpcService.class);
					services.add(rpcService);
				}
				rpcServiceCache.put(hostAndPortString, services);
			}
			// 监控节点数据变化
			this.etcdClient.watchChildren(serviceListKey, true, true,etcdServicesWatcher);
		}
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getEtcdUrl() {
		return etcdUrl;
	}

	public void setEtcdUrl(String etcdUrl) {
		this.etcdUrl = etcdUrl;
	}

	private String genConsumeKey(String group, String service, String version){
		return group+"_"+service+"_"+version;
	}

	private String genServiceConsumeKey(String service){
		return "/" + namespace + "/consumers/"+service;
	}

	private String genServiceApplicationConsumeKey(String service,String application){
		return "/" + namespace + "/consumers/"+service+"/"+application;
	}

	private String genServiceComsumeHostKey(String service,String application,String host){
		return "/" + namespace + "/consumers/"+service+"/"+application+"/"+host;
	}

	@Override
	public <T> void doRegisterRemote(String application,Class<T> iface, String version, String group) {
		String ip = this.getSelfIp();
		ConsumeRpcObject object = new ConsumeRpcObject();
		object.setApplication(application);
		object.setIp(ip);
		object.setGroup(group);
		object.setClassName(iface.getName());
		object.setVersion(version);

		consumeServices.add(object);

		if(this.etcdClient!=null){
			this.doUpload(object);
		}
	}

	private void doUpload(){
		for(ConsumeRpcObject object:consumeServices){
			this.doUpload();
		}
	}

	private void doUpload(ConsumeRpcObject object){
		String data = JSONUtils.toJSON(object);
		String service = this.genConsumeKey(object.getGroup(),object.getClassName(),object.getVersion());
		String hostDir = this.genServiceComsumeHostKey(service,object.getApplication(),object.getIp());
		this.etcdClient.set(hostDir,data);
	}

	@Override
	public List<String> getConsumeApplications(String group, String service, String version) {
		String serviceKey = this.genConsumeKey(group, service, version);
		String consumeDir = this.genServiceConsumeKey(serviceKey);
		EtcdResult result = this.etcdClient.children(consumeDir, false, false);

		if(result.isSuccess()){
			ArrayList<String> applications = new ArrayList<String>();
			List<EtcdNode> apps = result.getNode().getNodes();
			for(EtcdNode node:apps){
				String key = node.getKey();
				applications.add(key);
			}
			return applications;
		}else{
			throw new RpcException(result.getCause());
		}
	}

	/**
	 * 遍历拿到列表
	 * @param group
	 * @param service
	 * @param version
     * @return
     */
	@Override
	public List<ConsumeRpcObject> getConsumeObjects(String group, String service, String version) {
		String serviceKey = this.genConsumeKey(group, service, version);
		String consumeDir = this.genServiceConsumeKey(serviceKey);
		EtcdResult result = this.etcdClient.children(consumeDir, true, false);

		if(result.isSuccess()){
			List<EtcdNode> apps = result.getNode().getNodes();
			ArrayList<ConsumeRpcObject> list = new ArrayList<ConsumeRpcObject>();
			for(EtcdNode app:apps){
				if(app.isDir()){
					List<EtcdNode> hosts = app.getNodes();
					if(hosts!=null){
						for(EtcdNode host:hosts){
							if(!host.isDir()){
								String conJson = host.getValue();
								ConsumeRpcObject ccc = JSONUtils.fromJSON(conJson,ConsumeRpcObject.class);
								list.add(ccc);
							}
						}
					}
				}
			}
			return list;
		}else{
			throw new RpcException(result.getCause());
		}
	}

	private String genApplicationWeightkey(String application){
		return "/"+namespace+"/weight/"+application+"/weights";
	}

	private String genApplicationWeightWatchKey(String application){
		return "/"+namespace+"/weight/"+application+"/node";
	}

	private String genApplicationWeightHostkey(String application,String hostkey){
		return "/"+namespace+"/weight/"+application+"/weights/"+hostkey;
	}

	private void watchApplication(final String application){
		String applicationWeightWatchKey = this.genApplicationWeightWatchKey(application);
		this.etcdClient.watch(applicationWeightWatchKey, new EtcdWatchCallback() {
			@Override
			public void onChange(EtcdChangeResult future) {
				doGetWeights(application,true);
			}
		});
	}

	@Override
	public List<HostWeight> getWeights(String application) {
		return this.doGetWeights(application,false);
	}

	private List<HostWeight> doGetWeights(String application,boolean force){
		if(!force){
			List<HostWeight> hostWeights = this.applicationWeightMap.get(application);
			if(hostWeights!=null){
				return hostWeights;
			}
		}

		ArrayList<HostWeight> list = new ArrayList<HostWeight>();
		EtcdResult children = this.etcdClient.children(this.genApplicationWeightkey(application), false, false);
		if(children.isSuccess()&&children.getNode()!=null){
			EtcdNode etcdNode = children.getNode();
			List<EtcdNode> nodes = etcdNode.getNodes();
			if(nodes!=null&&nodes.size()>0){
				for(EtcdNode wnode:nodes){
					String key = wnode.getKey();
					String value = wnode.getValue();
					key = key.substring(key.lastIndexOf('/')+1);
					System.out.println("key:"+key);
					String[] hostport = key.split(":");
					HostWeight hostWeight = new HostWeight();
					hostWeight.setHost(hostport[0]);
					hostWeight.setPort(Integer.parseInt(hostport[1]));
					hostWeight.setWeight(Integer.parseInt(value));
					list.add(hostWeight);
				}
			}
		}

		this.applicationWeightMap.put(application,list);

		this.watchApplication(application);
		return list;
	}

	void setLimits(String application,List<LimitDefine> limits){
		String key = EtcdUtils.genLimitKey(namespace, application);
		String data = JSONUtils.toJSON(limits);
		etcdClient.set(key,data);
	}

	private void doSetWehgit(String application,String key,int weight,boolean override){
		String hostWeightKey = this.genApplicationWeightHostkey(application,key);
		String value = ""+weight;
		if(override){
			this.etcdClient.set(hostWeightKey,value);
		}else{
			EtcdResult result = this.etcdClient.get(hostWeightKey);
			if(result.isSuccess()){
				return;
			}
			//是否存在
			this.etcdClient.set(hostWeightKey,value);
		}
		//notify change
		String weightWatchKey = this.genApplicationWeightWatchKey(application);
		this.etcdClient.set(weightWatchKey,"weight_"+random.nextInt(10000000));
	}

	@Override
	public void setWeight(String application, HostWeight weight) {
		this.doSetWehgit(application,weight.getKey(),weight.getWeight(),true);
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean admin) {
		isAdmin = admin;
	}
}
