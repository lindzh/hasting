package com.lindzh.hasting.cluster.etcd;

import com.lindzh.hasting.cluster.HelloRpcService;
import com.lindzh.hasting.cluster.serializer.simple.SimpleSerializer;

public class EtcdClientTest {
	
	public static void main(String[] args) throws InterruptedException {
		
		EtcdRpcClient client = new EtcdRpcClient();
		client.setEtcdUrl("http://192.168.139.128:2911");
		client.setNamespace("myapp");
		client.setApplication("test");
		client.setSerializer(new SimpleSerializer());
		client.startService();
		HelloRpcService rpcService = client.register(HelloRpcService.class);
		
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
