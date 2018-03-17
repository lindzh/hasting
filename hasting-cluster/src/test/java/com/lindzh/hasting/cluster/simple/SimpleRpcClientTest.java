package com.lindzh.hasting.cluster.simple;

import com.lindzh.hasting.rpc.client.SimpleRpcClient;
import com.lindzh.hasting.cluster.HelloRpcService;
import com.lindzh.hasting.cluster.LoginRpcService;
import com.lindzh.hasting.cluster.TestBean;
import com.lindzh.hasting.cluster.TestRemoteBean;
import com.lindzh.hasting.cluster.serializer.simple.SimpleSerializer;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class SimpleRpcClientTest {
	
	public static void main(String[] args) throws InterruptedException {
		
		SimpleRpcClient client = new SimpleRpcClient();
		client.setHost("127.0.0.1");
		client.setPort(4321);

		client.setSerializer(new SimpleSerializer());
		
		client.startService();
		
		LoginRpcService loginRpcService = client.register(LoginRpcService.class);
		
		boolean login = loginRpcService.login("linda", "123456");
		
		System.out.println("login:"+login);

		HelloRpcService helloService = client.register(HelloRpcService.class);

		TestBean tb = new TestBean();
		tb.setLimit(10);
		tb.setMessage("hahahah");
		tb.setOffset(21);
		tb.setOrder("ttith6566");

		TestRemoteBean remoteBean = helloService.getBean(tb, 100);

		System.out.println(remoteBean);
		for(int i=0;i<1000;i++){
			try{
				HashSet<String> stringHashSet = new HashSet<String>();
//		stringHashSet.add("hfrg5rhrh");
				List<String> result = helloService.getString(stringHashSet);
				System.out.println(result);

				String[] rr  = helloService.hahahString(result.toArray(new String[0]));
				System.out.println(rr);

			}catch (Exception e){
				System.out.println(new Date());
				e.printStackTrace();
			}finally {
				Thread.currentThread().sleep(1000);
			}
		}
		client.stopService();
	}

}
