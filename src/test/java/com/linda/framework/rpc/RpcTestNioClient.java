package com.linda.framework.rpc;

import org.apache.log4j.Logger;

import com.linda.framework.rpc.client.SimpleClientRemoteExecutor;
import com.linda.framework.rpc.client.SimpleClientRemoteProxy;
import com.linda.framework.rpc.net.AbstractRpcConnector;
import com.linda.framework.rpc.nio.RpcNioConnector;

public class RpcTestNioClient {

	private static Logger logger = 	Logger.getLogger(RpcTestNioClient.class);
	
	public static void main(String[] args) {
		
		String host = "127.0.0.1";
		int port = 4332;
		AbstractRpcConnector connector = new RpcNioConnector();
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
		
		loginService.login("linda", "123456");
		
		helloRpcService.sayHello("this is HelloRpcService",564);
		
		testService.index(43, "index client test");
		
		String hello = helloRpcService.getHello();
		
		int ex = helloRpcService.callException(false);
		
		logger.info("hello result:"+hello);
	
		logger.info("exResult:"+ex);
	}
	
}
