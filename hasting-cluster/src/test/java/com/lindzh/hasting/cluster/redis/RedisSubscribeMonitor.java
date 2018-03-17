package com.lindzh.hasting.cluster.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public class RedisSubscribeMonitor {
	
	public static void main(String[] args) {
		Jedis jedis = new Jedis("192.168.139.129",7770);
		System.out.println("start subscribe----------");
		jedis.subscribe(new JedisPubSub(){
			@Override
			public void onMessage(String channel, String message) {
				System.out.println("channel:"+channel+" message:"+message);
			}

			@Override
			public void onPMessage(String pattern, String channel, String message) {
				System.out.println("channel:"+channel+" pattern:"+pattern+" message:"+message);
			}

			@Override
			public void onSubscribe(String channel, int subscribedChannels) {
				System.out.println("onSubscribe channel:"+channel+" subscribedChannels:"+subscribedChannels);
			}

			@Override
			public void onUnsubscribe(String channel, int subscribedChannels) {
				System.out.println("onUnsubscribe channel:"+channel+" subscribedChannels:"+subscribedChannels);
			}

			@Override
			public void onPUnsubscribe(String pattern, int subscribedChannels) {
				System.out.println("onPUnsubscribe pattern:"+pattern+" subscribedChannels:"+subscribedChannels);
			}

			@Override
			public void onPSubscribe(String pattern, int subscribedChannels) {
				System.out.println("onPSubscribe pattern:"+pattern+" subscribedChannels:"+subscribedChannels);
			}
			
		}, "default_rpc_cluster_notify_channel");
		jedis.close();
		
	}

}
