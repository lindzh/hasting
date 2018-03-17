package com.lindzh.hasting.cluster.redis;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import com.linda.framework.rpc.cluster.*;
import com.lindzh.hasting.rpc.cluster1.HostWeight;
import com.lindzh.hasting.rpc.cluster1.RpcClusterServer;
import com.lindzh.hasting.rpc.cluster1.RpcHostAndPort;
import com.lindzh.hasting.cluster.JSONUtils;
import com.lindzh.hasting.cluster.MD5Utils;
import com.lindzh.hasting.cluster.RpcClusterConst;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.log4j.Logger;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;

import com.lindzh.hasting.rpc.RpcService;
import com.lindzh.hasting.rpc.net.RpcNetBase;

/**
 * 
 * @author lindezhi
 * 利用redis publish channel + ttl 实现server列表动态变化
 * 配置管理与通知
 */
public class RedisRpcServer extends RpcClusterServer {
	
	private String namespace = "default";
	
	private RpcJedisDelegatePool jedisPool;
	
	private List<RpcService> rpcServiceCache = new ArrayList<RpcService>();
	
	private RpcNetBase network;
	
	private Timer timer = new Timer();

	private long notifyTtl = 3000;//默认5秒发送一次
	
	private long time = 0;
	
	private String serverMd5;

	private RpcHostAndPort myinfo = null;
	
	private Logger logger = Logger.getLogger(RedisRpcServer.class);
	
	public RedisRpcServer(){
		this.jedisPool = new RpcJedisDelegatePool();
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getRedisHost() {
		return jedisPool.getHost();
	}

	public void setRedisHost(String host) {
		jedisPool.setHost(host);
	}

	public int getRedisPort() {
		return jedisPool.getPort();
	}

	public void setRedisPort(int port) {
		jedisPool.setPort(port);
	}

	public Set<String> getRedisSentinels() {
		return jedisPool.getSentinels();
	}

	public void setRedisSentinels(Set<String> sentinels) {
		jedisPool.setSentinels(sentinels);
	}

	public String getRedisMasterName() {
		return jedisPool.getMasterName();
	}

	public void setRedisMasterName(String masterName) {
		jedisPool.setMasterName(masterName);
	}

	public GenericObjectPoolConfig getRedisPoolConfig() {
		return jedisPool.getPoolConfig();
	}

	public void setRedisPoolConfig(GenericObjectPoolConfig poolConfig) {
		jedisPool.setPoolConfig(poolConfig);
	}

	@Override
	public void onClose(RpcNetBase network, Exception e) {
		jedisPool.stopService();
		RedisRpcServer.this.notifyRpcServer(myinfo, RpcClusterConst.CODE_SERVER_STOP);
		this.setServerStop();
		this.stopHeartBeat();
	}
	
	@Override
	public void onStart(RpcNetBase network) {
		time = System.currentTimeMillis();
		this.startJedisAndAddHost(network);
		this.checkAndAddRpcService(network);
		this.notifyRpcServer(myinfo,RpcClusterConst.CODE_SERVER_START);
		this.network = network;

		myinfo = new RpcHostAndPort(network.getHost(), network.getPort());
		myinfo.setTime(time);
		myinfo.setApplication(this.getApplication());
		myinfo.setToken(this.getToken());

		this.startHeartBeat();

		HostWeight weight = new HostWeight();
		weight.setHost(this.getHost());
		weight.setPort(this.getPort());
		weight.setWeight(100);
		this.doSetWeight(getApplication(),weight,false);
	}
	
	private void stopHeartBeat(){
		timer.cancel();
		timer = null;
	}

	private void startHeartBeat(){
		Date start = new Date(System.currentTimeMillis()+1000L);
		timer.scheduleAtFixedRate(new HeartBeatTask(), start, notifyTtl);
	}
	
	private void setServerStop(){
		RedisUtils.executeRedisCommand(jedisPool, new JedisCallback(){
			public Object callback(Jedis jedis) {
				//删除服务列表
				final String key = RedisUtils.genServicesKey(namespace, serverMd5);
				jedis.del(key);
				final HostAndPort andPort = new HostAndPort(network.getHost(), network.getPort());
				final String json = JSONUtils.toJSON(andPort);
				//删除host
				jedis.srem(namespace+"_"+RpcClusterConst.RPC_REDIS_HOSTS_KEY, json);
				return null;
			}
		});
	}


	
	private void notifyRpcServer(RpcHostAndPort andPort,int messageType){
		int expire = (int)(notifyTtl*3)/1000;
		RedisUtils.notifyRpcServer(jedisPool,andPort,namespace,messageType,expire);
	}
	
	private void startJedisAndAddHost(RpcNetBase network){
		jedisPool.startService();
		final RpcHostAndPort andPort = new RpcHostAndPort(network.getHost(), network.getPort());
		andPort.setTime(time);
		andPort.setToken(this.getToken());

		final String json = JSONUtils.toJSON(andPort);
		this.serverMd5 = MD5Utils.hostMd5(this.getApplication(),network.getHost(),network.getPort());
		RedisUtils.executeRedisCommand(jedisPool,new JedisCallback(){
			public Object callback(Jedis jedis) {
				jedis.sadd(namespace+"_"+RpcClusterConst.RPC_REDIS_HOSTS_KEY, serverMd5);
				jedis.set(namespace+"_"+serverMd5, json);
				return null;
			}
		});
	}
	
	private void checkAndAddRpcService(final RpcNetBase network){
		if(rpcServiceCache.size()>0){
			RedisUtils.executeRedisCommand(jedisPool,new JedisCallback(){
				public Object callback(Jedis jedis) {
					String servicesKey = RedisUtils.genServicesKey(namespace, serverMd5);
					jedis.del(servicesKey);
					for(RpcService service:rpcServiceCache){
						RedisRpcServer.this.addRpcServiceTo(jedis,service,network);
					}
					return null;
				}
			});
		}
	}
	
	private void addRpcServiceTo(Jedis jedis,RpcService service,RpcNetBase network){
		if(network!=null){
			final String key = RedisUtils.genServicesKey(namespace, serverMd5);
			final String rpcService = JSONUtils.toJSON(service);
			if(jedis!=null){
				jedis.sadd(key,rpcService);
			}else{
				RedisUtils.executeRedisCommand(jedisPool,new JedisCallback(){
					public Object callback(Jedis jedis) {
						jedis.sadd(key,rpcService);
						return null;
					}
				});
			}
		}
	}

	@Override
	protected void doRegister(Class<?> clazz, Object ifaceImpl, String version,String group) {
		RpcService service = new RpcService(clazz.getName(),version,ifaceImpl.getClass().getName());
		service.setTime(System.currentTimeMillis());
		//添加application
		service.setApplication(this.getApplication());
		service.setGroup(group);

		if(this.network!=null){
			this.rpcServiceCache.add(service);
			this.addRpcServiceTo(null,service, network);
		}else{
			this.rpcServiceCache.add(service);
		}
	}
	
	private class HeartBeatTask extends TimerTask{
		@Override
		public void run() {
			RedisRpcServer.this.notifyRpcServer(myinfo, RpcClusterConst.CODE_SERVER_HEART);
		}
	}

	private String genApplicationServerWeightKey(String application){
		return this.namespace+"_weight_hosts_"+application;
	}

	private String genApplicationHostWeightKey(String application,String key){
		return this.namespace+"_weight_data_"+application+"_host_"+key;
	}

	private void doSetWeight(final String application, final HostWeight weight, final boolean override){
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
}
