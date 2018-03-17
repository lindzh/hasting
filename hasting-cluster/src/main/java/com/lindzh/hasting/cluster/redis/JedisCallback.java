package com.lindzh.hasting.cluster.redis;

import redis.clients.jedis.Jedis;

/**
 * 
 * @author lindezhi
 * redis unsafe call back
 */
public interface JedisCallback {
	
	public Object callback(Jedis jedis);

}
