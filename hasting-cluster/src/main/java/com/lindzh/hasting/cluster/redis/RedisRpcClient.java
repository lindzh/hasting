package com.lindzh.hasting.cluster.redis;

import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.lindzh.hasting.rpc.cluster1.RpcClusterClient;

public class RedisRpcClient extends RpcClusterClient{
	
	private RpcJedisDelegatePool jedisPool;
	
	private RedisRpcClientExecutor executor;
	
	public RedisRpcClient(){
		jedisPool = new RpcJedisDelegatePool();
		executor = new RedisRpcClientExecutor();
		executor.setJedisPool(jedisPool);
		super.setRemoteExecutor(executor);
	}

	public String getNamespace() {
		return executor.getNamespace();
	}

	public void setNamespace(String namespace) {
		executor.setNamespace(namespace);
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
}
