package com.lindzh.hasting.cluster.redis;

import java.util.Date;

import com.lindzh.hasting.cluster.HelloRpcService;
import com.lindzh.hasting.cluster.HelloRpcTestService;
import com.lindzh.hasting.cluster.LoginRpcService;

public class RpcJedisClientTest {

	public static void main(String[] args) throws InterruptedException {
		RedisRpcClient rpcClient = new RedisRpcClient();
		rpcClient.setRedisHost("127.0.0.1");
		rpcClient.setRedisPort(6379);

		rpcClient.setApplication("redis-client");
		HelloRpcTestService helloRpcTestService = rpcClient.register(HelloRpcTestService.class);
		HelloRpcService helloRpcService = rpcClient.register(HelloRpcService.class);
		LoginRpcService loginRpcService = rpcClient.register(LoginRpcService.class);

		rpcClient.startService();

		int idx = 1000;
		while(true){
			try{
				boolean login = loginRpcService.login("linda", "123456");
				System.out.println("login---:"+login);

				helloRpcService.sayHello("hihii  "+new Date(), idx);
				idx++;
				String index = helloRpcTestService.index(idx, "idx--"+new Date());
				System.out.println("index:"+index);
				idx++;
				Thread.currentThread().sleep(3000L);
			}catch(Exception e){
				e.printStackTrace();
				Thread.currentThread().sleep(3000L);
			}
		}
	}
	
}
