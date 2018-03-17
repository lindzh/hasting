package com.lindzh.hasting.cluster.etcd;

import com.lindzh.hasting.cluster.HelloRpcService;
import com.lindzh.hasting.cluster.HelloRpcServiceImpl;
import com.lindzh.hasting.cluster.HelloRpcTestService;
import com.lindzh.hasting.cluster.HelloRpcTestServiceImpl;
import com.lindzh.hasting.cluster.LoginRpcService;
import com.lindzh.hasting.cluster.LoginRpcServiceImpl;
import com.lindzh.hasting.cluster.serializer.simple.SimpleSerializer;

public class EtcdServerTest {
	
	public static void main(String[] args) {
		
		EtcdRpcServer rpcServer = new EtcdRpcServer();
		rpcServer.setEtcdUrl("http://192.168.139.128:2911");
		rpcServer.setNamespace("myapp");
		rpcServer.setApplication("simple");
		rpcServer.setSerializer(new SimpleSerializer());
		rpcServer.setPort(3354);
		rpcServer.register(HelloRpcService.class, new HelloRpcServiceImpl());
		rpcServer.register(LoginRpcService.class, new LoginRpcServiceImpl());
		rpcServer.register(HelloRpcTestService.class, new HelloRpcTestServiceImpl());
		rpcServer.startService();
		System.out.println("--------etcd client start---------");
	}

}
