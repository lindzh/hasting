package com.lindzh.hasting.rpc.aiorpc;

import com.lindzh.hasting.rpc.HelloRpcService;
import com.lindzh.hasting.rpc.HelloRpcServiceImpl;
import com.lindzh.hasting.rpc.HelloRpcTestService;
import com.lindzh.hasting.rpc.HelloRpcTestServiceImpl;
import com.lindzh.hasting.rpc.aio.RpcAioAcceptor;
import com.lindzh.hasting.rpc.server.SimpleRpcServer;

public class AioServerTest {
	
	public static void main(String[] args) {
		SimpleRpcServer server = new SimpleRpcServer();
		server.setHost("127.0.0.1");
		server.setPort(4321);
		server.setAcceptor(new RpcAioAcceptor());
		
		server.register(HelloRpcService.class, new HelloRpcServiceImpl());
		server.register(HelloRpcTestService.class, new HelloRpcTestServiceImpl());
		
		server.startService();

		System.out.println("server started");
	}
	

}
