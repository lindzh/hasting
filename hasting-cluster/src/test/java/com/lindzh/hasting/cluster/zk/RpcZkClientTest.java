package com.lindzh.hasting.cluster.zk;

import com.lindzh.hasting.cluster.HelloRpcService;

public class RpcZkClientTest {
	
	public static void main(String[] args) {
		ZkRpcClient client = new ZkRpcClient();
		client.setConnectString("127.0.0.1:2181");
		client.setNamespace("myrpc");
		client.setApplication("test");

		client.startService();
		HelloRpcService rpcService = client.register(HelloRpcService.class,null,"hello");




		
		int index = 50000;
		
		while(true){
			try{
				rpcService.sayHello("this is rpc etcd test-"+index, index);
				String hello = rpcService.getHello();
				System.out.println(hello);
				index++;
			}catch (Exception e){
				e.printStackTrace();
			}finally {
				try {
					Thread.sleep(1000L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
		
		
	}

}
