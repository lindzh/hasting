package com.lindzh.hasting.cluster.redis;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.lindzh.hasting.cluster.hash.RoundRobinHashing;
import com.lindzh.hasting.rpc.cluster1.AbstractRpcClusterClientExecutor;
import com.lindzh.hasting.rpc.cluster1.ConsumeRpcObject;
import com.lindzh.hasting.rpc.cluster1.HostWeight;
import com.lindzh.hasting.rpc.cluster1.RpcHostAndPort;
import com.lindzh.hasting.cluster.*;
import com.lindzh.hasting.cluster.hash.Hashing;
import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;

import com.lindzh.hasting.rpc.RpcService;
import com.lindzh.hasting.rpc.net.RpcNetBase;

/**
 * 
 * @author lindezhi
 * rpc 集群 redis通知
 */
public class RedisRpcClientExecutor extends AbstractRpcClusterClientExecutor implements MessageListener {
	
	private String namespace = "default";

	private RpcJedisDelegatePool jedisPool;
	
	private Timer timer = new Timer();
	
	private long checkTtl = 8000;

	private boolean isAdmin = false;
	
	private List<RpcHostAndPort> rpcServersCache = new ArrayList<RpcHostAndPort>();
	
	private Set<String> serverMd5s = new HashSet<String>();
	
	private Map<String,List<RpcService>> rpcServiceCache = new ConcurrentHashMap<String, List<RpcService>>();

	private Map<String,List<HostWeight>> applicationWeightMap = new HashMap<String,List<HostWeight>>();
	
	private Map<String,Long> heartBeanTimeCache = new ConcurrentHashMap<String,Long>();
	
	private SimpleJedisPubListener pubsubListener = new SimpleJedisPubListener();

	private HashSet<ConsumeRpcObject> consumeServices = new HashSet<ConsumeRpcObject>();
	
	private Hashing hashing = new RoundRobinHashing();
	
	private Logger logger = Logger.getLogger(RedisRpcClientExecutor.class);
	
	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public RpcJedisDelegatePool getJedisPool() {
		return jedisPool;
	}

	private boolean started = false;

	public void setJedisPool(RpcJedisDelegatePool jedisPool) {
		this.jedisPool = jedisPool;
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
		if(hostAndPort!=null){
			String key = hostAndPort.toString();
			return rpcServiceCache.get(key);
		}
		return null;
	}

	@Override
	public void startRpcCluster() {
		jedisPool.startService();
		this.startPubsubListener();
		this.startHeartBeat();
		this.fetchRpcServers();
		this.fetchRpcServices();

		if(!isAdmin){
			//admin不上传
			this.doUpload();
			this.doUploadServerInfo(RpcClusterConst.CODE_SERVER_START);
			startPublishServer();
		}
		started = true;
	}
	
	private void startPubsubListener(){
		pubsubListener.addListener(this);
		Jedis jedis = jedisPool.getResource();
		pubsubListener.setChannel(namespace+"_"+RpcClusterConst.RPC_REDIS_CHANNEL);
		pubsubListener.setJedis(jedis);
		pubsubListener.startService();
	}

	@Override
	public void stopRpcCluster() {
		started = false;

		this.stopHeartBeat();

		this.doUploadServerInfo(RpcClusterConst.CODE_SERVER_STOP);
		this.doDeleteConsumes();

		jedisPool.stopService();
		rpcServersCache = null;
		rpcServiceCache.clear();
		heartBeanTimeCache.clear();
	}

	private void startPublishServer(){
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				doUploadServerInfo(RpcClusterConst.CODE_SERVER_HEART);
			}
		},new Date(),checkTtl/3);
	}

	private void doUploadServerInfo(int messageType){
		int expire = (int)(checkTtl*2/1000);
		RedisUtils.notifyRpcServer(jedisPool,null,this.namespace,messageType,expire);
	}

	@Override
	public String hash(List<RpcHostAndPort> servers) {
		return hashing.hash(servers);
	}

	@Override
	public void onClose(RpcHostAndPort hostAndPort) {
		this.closeServer(hostAndPort);
	}
	
	private void closeServer(RpcHostAndPort hostAndPort){
		rpcServiceCache.remove(hostAndPort.toString());
		heartBeanTimeCache.remove(hostAndPort.toString());
		this.removeServer(hostAndPort);
	}
	
	private void removeServer(RpcHostAndPort hostAndPort){
		logger.info("removeServer "+hostAndPort.toString());
		super.removeServer(hostAndPort.toString());
		String hostAndPortStr = hostAndPort.toString();
		List<RpcHostAndPort> hostAndPorts = new ArrayList<RpcHostAndPort>();
		Set<String> newMd5s = new HashSet<String>();
		for(RpcHostAndPort hap:rpcServersCache){
			if(!hap.toString().equals(hostAndPortStr)){
				hostAndPorts.add(hap);
				String serverMd5 = MD5Utils.hostMd5(hap);
				newMd5s.add(serverMd5);
			}
		}
		synchronized (this) {
			rpcServersCache = hostAndPorts;
			serverMd5s = newMd5s;
		}
	}
	
	private void fetchRpcServers(){
		RedisUtils.executeRedisCommand(jedisPool, new JedisCallback(){
			public Object callback(Jedis jedis) {
				List<RpcHostAndPort> rpcServers = new ArrayList<RpcHostAndPort>();
				if(rpcServers!=null){
					Set<String> servers = jedis.smembers(namespace+"_"+RpcClusterConst.RPC_REDIS_HOSTS_KEY);
					if(servers!=null){
						serverMd5s = servers;
						for(String server:serverMd5s){
							String serverJson = jedis.get(namespace+"_"+server);
							if(serverJson!=null){
								RpcHostAndPort rpcHostAndPort = JSONUtils.fromJSON(serverJson, RpcHostAndPort.class);
								rpcServers.add(rpcHostAndPort);
							}
						}
					}
				}
				synchronized (RedisRpcClientExecutor.this) {
					rpcServersCache = rpcServers;
				}
				return null;
			}
		});
	}
	
	private void fetchRpcServices(){
		for(RpcHostAndPort hostAndPort:rpcServersCache){
			this.fetchRpcServices(hostAndPort);
		}
	}
	
	private void fetchRpcServices(final RpcHostAndPort hostAndPort){
		String serverMd5 = MD5Utils.hostMd5(hostAndPort);
		final String servicesKey = RedisUtils.genServicesKey(namespace, serverMd5);
		RedisUtils.executeRedisCommand(jedisPool, new JedisCallback(){
			public Object callback(Jedis jedis) {
				List<RpcService> rpcServices = new ArrayList<RpcService>();
				Set<String> services = jedis.smembers(servicesKey);
				if(services!=null){
					for(String service:services){
						RpcService rpcService = JSONUtils.fromJSON(service, RpcService.class);
						rpcServices.add(rpcService);
					}
				}
				rpcServiceCache.put(hostAndPort.toString(), rpcServices);
				return null;
			}
		});
	}

	@Override
	public void onMessage(RpcMessage message) {
		logger.info("onMessage:"+JSONUtils.toJSON(message));
		RpcHostAndPort hostAndPort = (RpcHostAndPort)message.getMessage();
		int messageType = message.getMessageType();
		if(messageType==RpcClusterConst.CODE_SERVER_STOP){
			this.closeServer(hostAndPort);
		}else if(messageType==RpcClusterConst.CODE_SERVER_HEART){
			this.serverAddOrHearBeat(hostAndPort);
		}else if(messageType==RpcClusterConst.CODE_SERVER_START){
			this.serverAddOrHearBeat(hostAndPort);
		}else if(messageType==RpcClusterConst.CODE_SERVER_ADD_RPC){
			this.fetchRpcServices(hostAndPort);
		}
	}
	
	private void serverAddOrHearBeat(RpcHostAndPort hostAndPort){
		Long time = heartBeanTimeCache.get(hostAndPort.toString());
		if(time!=null){
			long now = System.currentTimeMillis();
			if(now-time<checkTtl){
				heartBeanTimeCache.put(hostAndPort.toString(), System.currentTimeMillis());
				return;
			}
		}
		this.fetchRpcServers();
		heartBeanTimeCache.put(hostAndPort.toString(), System.currentTimeMillis());
		this.fetchRpcServices(hostAndPort);
		//动态更新集群
		this.startConnector(hostAndPort);
	}
	
	private void stopHeartBeat(){
		timer.cancel();
	}
	
	/**
	 * 启动心跳定时检测
	 */
	private void startHeartBeat(){
		timer.scheduleAtFixedRate(new HeartBeatTask(), checkTtl, checkTtl);
	}
	
	private void checkHeartBeat(){
		List<RpcHostAndPort> needRemoveServers = new ArrayList<RpcHostAndPort>();
		List<RpcHostAndPort> rpcServers = new ArrayList<RpcHostAndPort>(Arrays.asList(new RpcHostAndPort[rpcServersCache.size()]));
		Collections.copy(rpcServers, rpcServersCache);
		for(RpcHostAndPort server:rpcServers){
			Long beat = heartBeanTimeCache.get(server.toString());
			if(beat==null){
				needRemoveServers.add(server);
			}else{
				long now = System.currentTimeMillis();
				if(now-beat>checkTtl){
					needRemoveServers.add(server);
				}
			}
		}
		for(RpcHostAndPort removeServer:needRemoveServers){
			this.removeServer(removeServer);
		}
	}
	
	private class HeartBeatTask extends TimerTask{
		@Override
		public void run() {
			checkHeartBeat();
		}
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

		if(started){
			this.doUpload(consumeObject);
		}
	}

	private String genServiceConsumeAppsKey(String group, String service, String version){
		return this.namespace+"_consumers_"+group+"_"+service+"_"+version;
	}

	private String genServiceConsumeAppHostKey(String group, String service, String version,String app){
		return this.namespace+"_consumers_"+app+"_hosts_"+group+"_"+service+"_"+version;
	}

	/**
	 * 机器下线需要清除当前机器
	 */
	private void doDeleteConsumes(){
		for(final ConsumeRpcObject obj:consumeServices){
			final String consumeServiceAppHostkey = this.genServiceConsumeAppHostKey(obj.getGroup(),obj.getClassName(),obj.getVersion(),obj.getApplication());
			RedisUtils.executeRedisCommand(this.jedisPool, new JedisCallback() {
				@Override
				public Object callback(Jedis jedis) {
					jedis.srem(consumeServiceAppHostkey,obj.getIp());
					return "1";
				}
			});
		}
	}

	private void doUpload(final ConsumeRpcObject obj){
		final String serviceConsumeAppsKey = this.genServiceConsumeAppsKey(obj.getGroup(),obj.getClassName(),obj.getVersion());
		final String consumeServiceAppHostkey = this.genServiceConsumeAppHostKey(obj.getGroup(),obj.getClassName(),obj.getVersion(),obj.getApplication());
		RedisUtils.executeRedisCommand(this.jedisPool, new JedisCallback() {
			@Override
			public Object callback(Jedis jedis) {
				jedis.sadd(serviceConsumeAppsKey,obj.getApplication());
				return "1";
			}
		});
		RedisUtils.executeRedisCommand(this.jedisPool, new JedisCallback() {
			@Override
			public Object callback(Jedis jedis) {
				jedis.sadd(consumeServiceAppHostkey,obj.getIp());
				return "1";
			}
		});
	}

	private void doUpload(){
		for(final ConsumeRpcObject obj:consumeServices){
			this.doUpload(obj);
		}
	}

	@Override
	public List<String> getConsumeApplications(String group, String service, String version) {
		final String serviceConsumeAppKey = this.genServiceConsumeAppsKey(group, service, version);
		Object result = RedisUtils.executeRedisCommand(jedisPool, new JedisCallback() {
			@Override
			public Object callback(Jedis jedis) {
				Set<String> smembers = jedis.smembers(serviceConsumeAppKey);
				return smembers;
			}
		});
		Set<String> apps = (Set<String>)result;
		return new ArrayList<String>(apps);
	}

	@Override
	public List<ConsumeRpcObject> getConsumeObjects(final String group, final String service, final String version) {
		ArrayList<ConsumeRpcObject> list = new ArrayList<ConsumeRpcObject>();
		List<String> apps = this.getConsumeApplications(group, service, version);
		for(final String app:apps){
			final String appHostKey = this.genServiceConsumeAppHostKey(group,service,version,app);
			Object result = RedisUtils.executeRedisCommand(jedisPool, new JedisCallback() {
				@Override
				public Object callback(Jedis jedis) {
					ArrayList<ConsumeRpcObject> objects = new ArrayList<ConsumeRpcObject>();
					Set<String> smembers = jedis.smembers(appHostKey);
					if(smembers!=null){
						for(String mm:smembers){
							ConsumeRpcObject object = new ConsumeRpcObject();
							object.setIp(mm);
							object.setVersion(version);
							object.setClassName(service);
							object.setGroup(group);
							object.setApplication(app);
							objects.add(object);
						}
					}
					return objects;
				}
			});
			list.addAll((List<ConsumeRpcObject>)result);
		}
		return list;
	}

	private String genApplicationServerWeightKey(String application){
		return this.namespace+"_weight_hosts_"+application;
	}

	private String genApplicationHostWeightKey(String application,String key){
		return this.namespace+"_weight_data_"+application+"_host_"+key;
	}

	/**
	 * 定时获取application 权重列表
	 * @param application
     */
	private void watch(final String application){
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				doGetWeights(application,true);
			}
		},checkTtl*3);
	}


	private List<HostWeight> doGetWeights(final String application, boolean fromRegister){
		List<HostWeight> hostWeights = applicationWeightMap.get(application);
		if(!fromRegister&&hostWeights!=null&&hostWeights.size()>0){
			return hostWeights;
		}

		final String applicationServerWeightKey = this.genApplicationServerWeightKey(application);
		Object result = RedisUtils.executeRedisCommand(jedisPool, new JedisCallback() {
			@Override
			public Object callback(Jedis jedis) {
				ArrayList<HostWeight> result = new ArrayList<HostWeight>();
				Set<String> hosts = jedis.smembers(applicationServerWeightKey);
				if(hosts!=null&&hosts.size()>0){
					List<String> hostWeightKeys = new ArrayList<String>(hosts.size());
					for(String host:hosts){
						String hostWeightKey = genApplicationHostWeightKey(application, host);
						hostWeightKeys.add(hostWeightKey);
					}

					List<String> list = jedis.mget(hostWeightKeys.toArray(new String[0]));
					if(list!=null&&list.size()>0){
						for(String ww:list){
							HostWeight hostWeight = JSONUtils.fromJSON(ww, HostWeight.class);
							result.add(hostWeight);
						}
					}
				}
				return result;
			}
		});

		this.watch(application);

		if(result!=null){
			hostWeights = (List<HostWeight>)result;
			applicationWeightMap.put(application,hostWeights);
		}
		return hostWeights;
	}

	@Override
	public List<HostWeight> getWeights(String application) {
		return this.doGetWeights(application,false);
	}

	private void doSetWeight(final String application,final HostWeight weight,final boolean override){
		final String applicationServerWeightKey = this.genApplicationServerWeightKey(application);
		final String hostWeightKey = this.genApplicationHostWeightKey(application, weight.getKey());
		final String weightData = JSONUtils.toJSON(weight);
		RedisUtils.executeRedisCommand(jedisPool, new JedisCallback() {
			@Override
			public Object callback(Jedis jedis) {
				String weightValue = jedis.get(hostWeightKey);
				if(weightValue!=null){
					if(override){
						jedis.set(hostWeightKey,weightData);
					}
				}else{
					jedis.set(hostWeightKey,weightData);
					jedis.sadd(applicationServerWeightKey,weight.getKey());
				}
				return null;
			}
		});
	}

	@Override
	public void setWeight(String application, HostWeight weight) {
		this.doSetWeight(application,weight,true);
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean admin) {
		isAdmin = admin;
	}
}
