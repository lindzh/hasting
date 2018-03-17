package com.lindzh.hasting.cluster.redis;

import java.util.ArrayList;
import java.util.List;

import com.lindzh.hasting.cluster.JSONUtils;
import com.lindzh.hasting.cluster.MessageListener;
import com.lindzh.hasting.cluster.RpcMessage;
import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import com.fasterxml.jackson.core.type.TypeReference;
import com.lindzh.hasting.rpc.Service;
import com.lindzh.hasting.rpc.cluster1.RpcHostAndPort;

/**
 * redis pub sub 接收集群消息心跳
 * @author lindezhi
 *
 */
public class SimpleJedisPubListener extends JedisPubSub implements Service,Runnable{
	
	private Jedis jedis;
	
	private Thread messageReceiveThread;

	private String channel;
	
	private List<MessageListener> listeners = new ArrayList<MessageListener>();
	
	private Logger logger = Logger.getLogger(SimpleJedisPubListener.class);
	
	public Jedis getJedis() {
		return jedis;
	}

	public void setJedis(Jedis jedis) {
		this.jedis = jedis;
	}
	
	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public void fireListeners(RpcMessage message){
		for(MessageListener listener:listeners){
			listener.onMessage(message);
		}
	}
	
	public void addListener(MessageListener listener){
		this.listeners.add(listener);
	}
	
	@Override
	public void onMessage(String channel, String message) {
		RpcMessage<RpcHostAndPort> rpcMessage = JSONUtils.fromJSON(message, new TypeReference<RpcMessage<RpcHostAndPort>>(){});
		this.fireListeners(rpcMessage);
	}

	@Override
	public void onPMessage(String pattern, String channel, String message) {
		
	}

	@Override
	public void onSubscribe(String channel, int subscribedChannels) {
		
	}

	@Override
	public void onUnsubscribe(String channel, int subscribedChannels) {
		
	}

	@Override
	public void onPUnsubscribe(String pattern, int subscribedChannels) {
		
	}

	@Override
	public void onPSubscribe(String pattern, int subscribedChannels) {
		
	}

	@Override
	public void startService() {
		messageReceiveThread = new Thread(this);
		messageReceiveThread.start();
	}

	@Override
	public void stopService() {
		this.unsubscribe();
		jedis.close();
	}

	@Override
	public void run() {
		logger.info("subscribe:"+channel);
		jedis.subscribe(this, channel);
	}
}
