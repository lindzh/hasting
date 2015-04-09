package com.linda.framework.rpc.generic;

import com.linda.framework.rpc.HelloRpcService;
import com.linda.framework.rpc.HelloRpcServiceImpl;
import com.linda.framework.rpc.server.SimpleRpcServer;

public class GenericServerTest {

	public static void main(String[] args) {
		SimpleRpcServer server = new SimpleRpcServer();
		server.setHost("0.0.0.0");
		server.setPort(4445);
		server.register(HelloRpcService.class, new HelloRpcServiceImpl());
		server.startService();
		System.out.println("server startup");
	}
	
}
