package com.linda.framework.rpc.aiorpc;

import com.linda.framework.rpc.HelloRpcService;
import com.linda.framework.rpc.HelloRpcTestService;
import com.linda.framework.rpc.aio.RpcAioConnector;
import com.linda.framework.rpc.client.SimpleRpcClient;

public class AioClientTest {
	
	public static void main(String[] args) {
		
		SimpleRpcClient client = new SimpleRpcClient();
		client.setHost("127.0.0.1");
		client.setPort(4321);
		
		client.setConnectorClass(RpcAioConnector.class);
		
		client.startService();
		
		HelloRpcService helloRpcService = client.register(HelloRpcService.class);
		
		helloRpcService.sayHello("this is a test", 123);
		
		HelloRpcTestService testService = client.register(HelloRpcTestService.class);
		String index = testService.index(12345, "haha");
		System.out.println("resp:"+index);
		
	}

}
