package com.linda.framework.rpc.service;

import org.apache.log4j.Logger;

import com.linda.framework.rpc.HelloRpcService;
import com.linda.framework.rpc.HelloRpcServiceImpl;
import com.linda.framework.rpc.HelloRpcTestService;
import com.linda.framework.rpc.HelloRpcTestServiceImpl;
import com.linda.framework.rpc.LoginRpcService;
import com.linda.framework.rpc.LoginRpcServiceImpl;
import com.linda.framework.rpc.MyTestRpcFilter;
import com.linda.framework.rpc.RpcLoginCheckFilter;
import com.linda.framework.rpc.server.RpcServer;

public class RpcServerTest {
	
	private static Logger logger = 	Logger.getLogger(RpcServerTest.class);
	
	public static void main(String[] args) {
		
		String host = "127.0.0.1";
		int port = 4332;
		RpcServer server = new RpcServer();
		
		server.setHost(host);
		server.setPort(port);
		
		Object obj = new HelloRpcServiceImpl();
		
		server.register(HelloRpcService.class, obj);
		
		HelloRpcTestServiceImpl obj2 = new HelloRpcTestServiceImpl();
		
		server.register(HelloRpcTestService.class, obj2);
		
		LoginRpcService loginService = new LoginRpcServiceImpl();
		
		server.register(LoginRpcService.class, loginService);
		
		server.addRpcFilter(new MyTestRpcFilter());
		
		server.addRpcFilter(new RpcLoginCheckFilter());
		
		server.startService();
		
		logger.info("service started");
	}

}
