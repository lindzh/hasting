package com.lindzh.hasting.rpc.generic;

import com.lindzh.hasting.rpc.HelloRpcService;
import com.lindzh.hasting.rpc.HelloRpcServiceImpl;
import com.lindzh.hasting.rpc.oio.RpcOioAcceptor;
import com.lindzh.hasting.rpc.server.SimpleRpcServer;
import com.lindzh.hasting.rpc.utils.RpcUtils;

public class GenericServerTest {

	public static void main(String[] args) {
		SimpleRpcServer server = new SimpleRpcServer();
		server.addRpcFilter(new RpcContextClearFilter());
		server.setAcceptor(new RpcOioAcceptor());
		server.setHost("127.0.0.1");
		server.setPort(4445);

		HelloRpcService helloService = new HelloRpcServiceImpl();
		
		server.register(HelloRpcService.class, new HelloRpcServiceImpl(), RpcUtils.DEFAULT_VERSION,"aapp");
		server.startService();
		System.out.println("server startup");
	}
	
}
