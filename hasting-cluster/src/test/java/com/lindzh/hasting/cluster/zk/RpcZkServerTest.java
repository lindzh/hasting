package com.lindzh.hasting.cluster.zk;

import com.lindzh.hasting.cluster.HelloRpcService;
import com.lindzh.hasting.cluster.HelloRpcServiceImpl;
import com.lindzh.hasting.cluster.HelloRpcTestServiceImpl;
import com.lindzh.hasting.cluster.LoginRpcServiceImpl;
import com.lindzh.hasting.cluster.HelloRpcTestService;
import com.lindzh.hasting.cluster.LoginRpcService;

public class RpcZkServerTest {
	
	public static void main(String[] args) {
		ZkRpcServer rpcServer = new ZkRpcServer();
		rpcServer.setConnectString("127.0.0.1:2181");
		rpcServer.setNamespace("myrpc");
		rpcServer.setHost("127.0.0.1");
		rpcServer.setPort(3335);
		rpcServer.setApplication("myapp");
		rpcServer.setValidateToken(true);
		rpcServer.register(HelloRpcService.class, new HelloRpcServiceImpl(),null,"hello");
		rpcServer.register(LoginRpcService.class, new LoginRpcServiceImpl(),null,"hello");
		rpcServer.register(HelloRpcTestService.class, new HelloRpcTestServiceImpl(),null,"hello");
		rpcServer.startService();
		System.out.println("--------started----------");
	}

}
