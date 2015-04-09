package com.linda.framework.rpc.generic;

import com.linda.framework.rpc.HelloRpcService;
import com.linda.framework.rpc.HelloRpcServiceImpl;
import com.linda.framework.rpc.oio.RpcOioAcceptor;
import com.linda.framework.rpc.server.SimpleRpcServer;

public class GenericServerTest {

	public static void main(String[] args) {
		SimpleRpcServer server = new SimpleRpcServer();
		server.setAcceptor(new RpcOioAcceptor());
		server.setHost("0.0.0.0");
		server.setPort(4445);
		server.register(HelloRpcService.class, new HelloRpcServiceImpl());
		server.startService();
		System.out.println("server startup");
	}
	
}
