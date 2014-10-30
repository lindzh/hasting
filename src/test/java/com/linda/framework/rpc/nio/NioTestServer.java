package com.linda.framework.rpc.nio;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.linda.framework.rpc.RpcObject;
import com.linda.framework.rpc.net.AbstractRpcAcceptor;
import com.linda.framework.rpc.net.RpcCallListener;
import com.linda.framework.rpc.net.RpcSender;

public class NioTestServer implements RpcCallListener{
	
	private static Logger logger = Logger.getLogger(NioTestServer.class);
	private static AtomicInteger receive = new AtomicInteger(0);
	private static ConcurrentHashMap<RpcSender, AtomicInteger> count = new ConcurrentHashMap<RpcSender, AtomicInteger>();
	
	public static void main(String[] args) throws InterruptedException {
		NioTestServer server = new NioTestServer();
		
		String host = "127.0.0.1";
		int port = 4332;
		
		AbstractRpcAcceptor acceptor = new RpcNioAcceptor();
		acceptor.setHost(host);
		acceptor.setPort(port);
		acceptor.addRpcCallListener(server);
		acceptor.startService();
		Thread.currentThread().sleep(60000);
		logger.info("receive count all:"+receive.get());
		Enumeration<RpcSender> keys = count.keys();
		while(keys.hasMoreElements()){
			RpcSender sender = keys.nextElement();
			AtomicInteger c = count.get(sender);
			logger.info("count:"+c.get());
		}
	}

	@Override
	public void onRpcMessage(RpcObject rpc, RpcSender sender) {
		logger.info("rpc server receive:"+rpc);
		sender.sendRpcObject(rpc, 1000);
		AtomicInteger c = count.get(sender);
		if(c==null){
			c = new AtomicInteger(1);
			count.put(sender, c);
		}else{
			c.incrementAndGet();
		}
		
		receive.incrementAndGet();
	}

}
