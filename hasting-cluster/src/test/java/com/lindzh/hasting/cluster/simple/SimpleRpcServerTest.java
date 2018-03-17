package com.lindzh.hasting.cluster.simple;

import com.lindzh.hasting.cluster.limit.LimitCache;
import com.lindzh.hasting.cluster.limit.LimitConst;
import com.lindzh.hasting.cluster.limit.LimitFilter;
import com.lindzh.hasting.cluster.serializer.simple.SimpleSerializer;
import com.lindzh.hasting.cluster.HelloRpcService;
import com.lindzh.hasting.cluster.HelloRpcServiceImpl;
import com.lindzh.hasting.cluster.HelloRpcTestService;
import com.lindzh.hasting.cluster.HelloRpcTestServiceImpl;
import com.lindzh.hasting.cluster.LoginRpcService;
import com.lindzh.hasting.cluster.LoginRpcServiceImpl;
import com.lindzh.hasting.cluster.limit.LimitDefine;
import com.lindzh.hasting.rpc.server.SimpleRpcServer;

import java.util.ArrayList;

public class SimpleRpcServerTest {
	
	public static void main(String[] args) {
		
		SimpleRpcServer rpcServer = new SimpleRpcServer();
		rpcServer.setHost("127.0.0.1");
		rpcServer.setPort(4321);
		LimitCache limitCache = new LimitCache();
		ArrayList<LimitDefine> limitDefines = new ArrayList<LimitDefine>();
		LimitDefine define = new LimitDefine();
		define.setType(LimitConst.LIMIT_ALL);
		define.setCount(5);
		define.setTtl(7000);
		limitDefines.add(define);

		limitCache.addOrUpdate(limitDefines);

		rpcServer.addRpcFilter(new LimitFilter(limitCache));


		rpcServer.setSerializer(new SimpleSerializer());
		
		rpcServer.register(HelloRpcService.class, new HelloRpcServiceImpl());
		rpcServer.register(LoginRpcService.class, new LoginRpcServiceImpl());
		rpcServer.register(HelloRpcTestService.class, new HelloRpcTestServiceImpl());
		rpcServer.startService();
		System.out.println("--------started----------");
		
	}

}
