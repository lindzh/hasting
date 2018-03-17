package com.lindzh.hasting.rpc;

import org.apache.log4j.Logger;

import com.lindzh.hasting.rpc.client.SimpleClientRemoteExecutor;
import com.lindzh.hasting.rpc.client.SimpleClientRemoteProxy;
import com.lindzh.hasting.rpc.net.AbstractRpcConnector;
import com.lindzh.hasting.rpc.nio.RpcNioConnector;

public class RpcTestNioClient {

	private static Logger logger = 	Logger.getLogger(RpcTestNioClient.class);
	
	public static void main(String[] args) {
		
		String host = "127.0.0.1";
		int port = 4332;
		AbstractRpcConnector connector = new RpcNioConnector(null);
		connector.setHost(host);
		connector.setPort(port);
		
		SimpleClientRemoteExecutor executor = new SimpleClientRemoteExecutor(connector);
		
		SimpleClientRemoteProxy proxy = new SimpleClientRemoteProxy();
		
		proxy.setRemoteExecutor(executor);
		
		proxy.startService();
		
		LoginRpcService loginService = proxy.registerRemote(LoginRpcService.class);
		
		HelloRpcService helloRpcService = proxy.registerRemote(HelloRpcService.class);
		
		HelloRpcTestService testService = proxy.registerRemote(HelloRpcTestService.class);
		
		logger.info("start client");
		
		helloRpcService.sayHello("this is HelloRpcService",564);
		
		helloRpcService.sayHello("this is HelloRpcService tttttttt",3333);
		
		boolean login = loginService.login("linda", "123456");
		
		logger.info("login result:"+login);
		
		String index = testService.index(43, "index client test");
		
		logger.info("index result:"+index);
		
		String hello = helloRpcService.getHello();
		
		int ex = helloRpcService.callException(false);
		
		logger.info("hello result:"+hello);
	
		logger.info("exResult:"+ex);
		
		long start = System.currentTimeMillis();
		int total = 10000;
		for(int i=0;i<total;i++){
			helloRpcService.sayHello("this is HelloRpcService",564);
		}
		long end = System.currentTimeMillis();
		System.out.println("cost:"+(end-start));
		
		
	}
	
}
