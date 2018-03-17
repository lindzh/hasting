package com.lindzh.hasting.cluster.redis;

import com.lindzh.hasting.cluster.LoginRpcServiceImpl;
import com.lindzh.hasting.cluster.HelloRpcService;
import com.lindzh.hasting.cluster.HelloRpcServiceImpl;
import com.lindzh.hasting.cluster.HelloRpcTestService;
import com.lindzh.hasting.cluster.HelloRpcTestServiceImpl;
import com.lindzh.hasting.cluster.LoginRpcService;

public class RpcJedisClusterTest {
	
	public static void main(String[] args) {
		RedisRpcServer rpcServer = new RedisRpcServer();
		rpcServer.setPort(3323);
		rpcServer.setHost("127.0.0.1");
		rpcServer.setRedisHost("127.0.0.1");
		rpcServer.setRedisPort(6379);
		rpcServer.setApplication("redis-server");
		rpcServer.register(HelloRpcService.class, new HelloRpcServiceImpl());
		rpcServer.register(LoginRpcService.class, new LoginRpcServiceImpl());
		rpcServer.register(HelloRpcTestService.class, new HelloRpcTestServiceImpl());
		rpcServer.startService();
		System.out.println("--------started----------");
	}

}
