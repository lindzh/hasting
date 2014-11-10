package com.linda.framework.rpc.service;

import org.apache.log4j.Logger;

import com.linda.framework.rpc.HelloRpcService;
import com.linda.framework.rpc.HelloRpcTestService;
import com.linda.framework.rpc.LoginRpcService;
import com.linda.framework.rpc.client.RpcClient;

public class RpcClientTest {
	
	private static Logger logger = 	Logger.getLogger(RpcClientTest.class);
	
	public static void main(String[] args) {
		String host = "127.0.0.1";
		int port = 4332;
		RpcClient client = new RpcClient();
		
		client.setHost(host);
		client.setPort(port);
		
		client.startService();
		
		LoginRpcService loginService = client.register(LoginRpcService.class);
		
		HelloRpcService helloRpcService = client.register(HelloRpcService.class);
		
		HelloRpcTestService testService = client.register(HelloRpcTestService.class);
		
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
		
		client.stopService();
		
		System.exit(0);
	}

}
