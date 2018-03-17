package com.lindzh.hasting.cluster.redis;

import java.util.Set;

import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.util.Pool;

import com.lindzh.hasting.rpc.Service;
import com.lindzh.hasting.rpc.exception.RpcException;

/**
 * 
 * @author lindezhi
 * 统一获取jedis pool代理 支持sentinels和单节点部署
 */
public class RpcJedisDelegatePool extends Pool<Jedis> implements Service{
	
	private String host;
	
	private int port;
	
	private Set<String> sentinels;
	
	private String masterName;
	
	private Pool<Jedis> redisPool;
	
	private GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
	
	@Override
	public void initPool(GenericObjectPoolConfig poolConfig,
			PooledObjectFactory<Jedis> factory) {
		redisPool.initPool(poolConfig, factory);
	}

	@Override
	public Jedis getResource() {
		return redisPool.getResource();
	}

	@Override
	public void returnResourceObject(Jedis resource) {
		redisPool.returnResourceObject(resource);
	}

	@Override
	public void returnBrokenResource(Jedis resource) {
		redisPool.returnBrokenResource(resource);
	}

	@Override
	public void returnResource(Jedis resource) {
		redisPool.returnResource(resource);
	}

	@Override
	public void destroy() {
		redisPool.destroy();
	}

	@Override
	public void startService() {
		if(redisPool==null){
			if(host!=null){
				redisPool = new JedisPool(poolConfig,host, port);
			}else if(sentinels!=null){
				redisPool = new JedisSentinelPool(masterName, sentinels, poolConfig);
			}else{
				throw new RpcException("can't init a redis server");
			}
		}
	}

	@Override
	public void stopService() {
		if(redisPool!=null){
			this.redisPool.destroy();
		}
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Set<String> getSentinels() {
		return sentinels;
	}

	public void setSentinels(Set<String> sentinels) {
		this.sentinels = sentinels;
	}

	public String getMasterName() {
		return masterName;
	}

	public void setMasterName(String masterName) {
		this.masterName = masterName;
	}

	public GenericObjectPoolConfig getPoolConfig() {
		return poolConfig;
	}

	public void setPoolConfig(GenericObjectPoolConfig poolConfig) {
		this.poolConfig = poolConfig;
	}
}
