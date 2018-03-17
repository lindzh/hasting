package com.lindzh.hasting.rpc.aiorpc;

import com.lindzh.hasting.rpc.HelloRpcService;
import com.lindzh.hasting.rpc.HelloRpcTestService;
import com.lindzh.hasting.rpc.aio.RpcAioConnector;
import com.lindzh.hasting.rpc.client.SimpleRpcClient;

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
