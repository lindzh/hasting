package com.lindzh.hasting.cluster.redis;

import com.linda.framework.rpc.cluster.*;
import com.lindzh.hasting.rpc.cluster1.RpcHostAndPort;
import com.lindzh.hasting.cluster.JSONUtils;
import com.lindzh.hasting.cluster.MD5Utils;
import com.lindzh.hasting.cluster.RpcClusterConst;
import com.lindzh.hasting.cluster.RpcMessage;
import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;

public class RedisUtils {
	
	private static Logger logger = Logger.getLogger(RedisUtils.class);
	
	public static Object executeRedisCommand(RpcJedisDelegatePool jedisPool,JedisCallback call){
		try{
			Jedis jedis = jedisPool.getResource();
			try{
				Object result = call.callback(jedis);
				jedisPool.returnResource(jedis);
				return result;
			}catch(Exception e){
				logger.error("call error",e);
				jedisPool.returnBrokenResource(jedis);
				return null;
			}
		}catch(Exception e){
			logger.error("redis get resource error ",e);
		}
		return null;
	}
	
	public static String genServicesKey(String namespace,String md5){
		return RpcClusterConst.RPC_REDIS_SERVER_SERVICE_PREFIX+"_"+namespace+"_"+md5;
	}

	public static <T> void publish(RpcMessage<T> message, RpcJedisDelegatePool jedisPool, final String key, final String channel, final int expire){
		final String json = JSONUtils.toJSON(message);
		RedisUtils.executeRedisCommand(jedisPool,new JedisCallback(){
			public Object callback(Jedis jedis) {
				jedis.expire(key, expire);
				jedis.publish(channel, json);
				logger.info("publish "+channel+" message:"+json);
				return null;
			}
		});
	}

	public static void notifyRpcServer(RpcJedisDelegatePool jedisPool, RpcHostAndPort host, String namespace, int messageType, int expire){
		if(host==null){
			return;
		}
		RpcMessage<RpcHostAndPort> rpcMessage = new RpcMessage<RpcHostAndPort>(messageType,host);
		String hostMd5 = MD5Utils.hostMd5(host);
		String servicesKey = RedisUtils.genServicesKey(namespace, hostMd5);
		String channel = namespace+"_"+RpcClusterConst.RPC_REDIS_CHANNEL;
		RedisUtils.publish(rpcMessage,jedisPool,servicesKey,channel,expire);
	}
}
