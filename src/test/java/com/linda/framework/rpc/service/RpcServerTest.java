package com.linda.framework.rpc.service;

import java.util.HashSet;

import org.apache.log4j.Logger;

import com.linda.framework.rpc.HelloRpcService;
import com.linda.framework.rpc.HelloRpcServiceImpl;
import com.linda.framework.rpc.HelloRpcTestService;
import com.linda.framework.rpc.HelloRpcTestServiceImpl;
import com.linda.framework.rpc.LoginRpcService;
import com.linda.framework.rpc.LoginRpcServiceImpl;
import com.linda.framework.rpc.MyTestRpcFilter;
import com.linda.framework.rpc.RemoteCall;
import com.linda.framework.rpc.RpcObject;
import com.linda.framework.rpc.filter.RpcFilter;
import com.linda.framework.rpc.filter.RpcFilterChain;
import com.linda.framework.rpc.net.RpcSender;
import com.linda.framework.rpc.server.AbstractRpcServer;
import com.linda.framework.rpc.server.ConcurrentRpcServer;

public class RpcServerTest {
	
	private static Logger logger = 	Logger.getLogger(RpcServerTest.class);
	
	private static class ClientFilter implements RpcFilter{

		private HashSet<String> hosts = new HashSet<String>();
		
		@Override
		public void doFilter(RpcObject rpc, RemoteCall call, RpcSender sender,
				RpcFilterChain chain) {
			String host = rpc.getHost()+":"+rpc.getPort();
			hosts.add(host);
			chain.nextFilter(rpc, call, sender);
		}
		
	}
	
	public static void main(String[] args) throws InterruptedException {
		
		long sleep = 300000;
		
		String host = "0.0.0.0";
		int port = 4332;
		
		int threadCount = 20;
		
		if(args!=null){
			for(String arg:args){
				if(arg.startsWith("-h")){
					host = arg.substring(2);
				}else if(arg.startsWith("-p")){
					port = Integer.parseInt(arg.substring(2));
				}else if(arg.startsWith("-s")){
					sleep = Long.parseLong(arg.substring(2));
				}else if(arg.startsWith("-th")){
					threadCount = Integer.parseInt(arg.substring(3));
				}
			}
		}
		
		AbstractRpcServer server = new ConcurrentRpcServer();
		//server.setAcceptor(new RpcOioAcceptor());
		server.setHost(host);
		server.setPort(port);
		
		server.setExecutorThreadCount(threadCount);
		
		HelloRpcService helloRpcServiceImpl = new HelloRpcServiceImpl();
		
		server.register(HelloRpcService.class, helloRpcServiceImpl);
		
		HelloRpcTestServiceImpl obj2 = new HelloRpcTestServiceImpl();
		
		server.register(HelloRpcTestService.class, obj2);
		
		LoginRpcService loginService = new LoginRpcServiceImpl();
		
		server.register(LoginRpcService.class, loginService);
		
		server.addRpcFilter(new MyTestRpcFilter());
		
		//server.addRpcFilter(new RpcLoginCheckFilter());
		
		ClientFilter clientFilter = new ClientFilter();
		
		server.addRpcFilter(clientFilter);
		
		server.startService();
		
		logger.info("service started");
		
		Thread.currentThread().sleep(sleep);
		
		server.stopService();
		
		logger.info("clients:"+clientFilter.hosts);
		
		logger.info("clientsSize:"+clientFilter.hosts.size());
		
		System.exit(0);
	}

}
