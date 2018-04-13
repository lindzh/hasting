package com.lindzh.hasting.netty.generic;


import com.lindzh.hasting.netty.HelloRpcService;
import com.lindzh.hasting.netty.HelloRpcServiceImpl;
import com.lindzh.hasting.netty.RpcNettyAcceptor;
import com.lindzh.hasting.rpc.server.SimpleRpcServer;

public class GenericServerTest {

	public static void main(String[] args) {
		SimpleRpcServer server = new SimpleRpcServer();
		server.setAcceptor(new RpcNettyAcceptor());
		server.setHost("0.0.0.0");
		server.setPort(4445);
		server.register(HelloRpcService.class, new HelloRpcServiceImpl());
		server.startService();
		System.out.println("server startup");
	}
	
}
