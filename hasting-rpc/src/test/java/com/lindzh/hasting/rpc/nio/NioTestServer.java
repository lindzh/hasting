package com.lindzh.hasting.rpc.nio;

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.lindzh.hasting.rpc.RpcObject;
import com.lindzh.hasting.rpc.net.RpcCallListener;
import com.lindzh.hasting.rpc.net.RpcSender;

public class NioTestServer implements RpcCallListener{
	
	private static Logger logger = Logger.getLogger(NioTestServer.class);
	RpcNioAcceptor acceptor;
	private SimpleRpcNioSelector selection;
	private String host;
	private int port;
	private AtomicInteger receive = new AtomicInteger(0);
	private AtomicBoolean started = new AtomicBoolean(false);
	private ConcurrentHashMap<String, AtomicInteger> count = new ConcurrentHashMap<String, AtomicInteger>();
	
	public NioTestServer(SimpleRpcNioSelector selection){
		this.selection = selection;
	}
	
	public void startService(){
		if(!started.get()){
			acceptor = new RpcNioAcceptor(selection);
			acceptor.setHost(host);
			acceptor.setPort(port);
			acceptor.addRpcCallListener(this);
			acceptor.startService();
			started.set(true);
		}
	}
	
	public void printResult(){
		String hostname = host+":"+port;
		logger.info(hostname+" receive count all:"+receive.get());
		Enumeration<String> keys = count.keys();
		int i=1;
		while(keys.hasMoreElements()){
			String sender = keys.nextElement();
			AtomicInteger c = count.get(sender);
			logger.info("host:"+hostname+" client "+sender+" count:"+c.get());
			i++;
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		SimpleRpcNioSelector selection = new SimpleRpcNioSelector();
		String ip = "127.0.0.1";
		int port = 3333;
		int c = 5;
		List<NioTestServer> servers = createServers(selection,c,ip,port);
		startService(servers);
		Thread.currentThread().sleep(80000);
		printResult(servers);
	}
	
	public static List<NioTestServer> createServers(SimpleRpcNioSelector selection,int c,String ip,int basePort){
		if(selection==null){
			selection = new SimpleRpcNioSelector();
		}
		List<NioTestServer> servers = new LinkedList<NioTestServer>();
		int i = 0;
		while(i<c){
			NioTestServer server = new NioTestServer(selection);
			server.host = ip;
			server.port = basePort+i;
			i++;
			servers.add(server);
		}
		return servers;
	}

	public static void startService(List<NioTestServer> servers){
		for(NioTestServer server:servers){
			server.startService();
		}
	}
	
	public static void printResult(List<NioTestServer> servers){
		for(NioTestServer server:servers){
			server.printResult();
		}
	}
	
	public void stopService(){
		acceptor.stopService();
	}
	
	@Override
	public void onRpcMessage(RpcObject rpc, RpcSender sender) {
		sender.sendRpcObject(rpc, 1000);
		RpcNioConnector connector = (RpcNioConnector)sender;
		String clientKey = connector.getRemoteHost()+":"+connector.getRemotePort();
		AtomicInteger c = count.get(clientKey);
		if(c==null){
			c = new AtomicInteger(1);
			count.put(clientKey, c);
		}else{
			c.incrementAndGet();
		}
		receive.incrementAndGet();
	}

}
