package com.lindzh.hasting.cluster;

import java.util.HashMap;

import com.lindzh.hasting.rpc.HelloRpcService;
import com.lindzh.hasting.rpc.cluster1.RpcClusterClient;
import com.lindzh.hasting.rpc.generic.GenericService;
import com.lindzh.hasting.rpc.utils.RpcUtils;

public class SimpleClientTest {
	
	public static void main(String[] args) {
		RpcClusterClient client = new RpcClusterClient();
		client.setRemoteExecutor(new SimpleClusterExecutor());
		client.startService();
		HelloRpcService service = client.register(HelloRpcService.class);
		service.sayHello("this is linda", 32);
		GenericService genService = client.register(GenericService.class);
		
		String[] getBeanTypes = new String[]{"TestBean","int"};
		HashMap<String,Object> map = new HashMap<String,Object>();
		map.put("limit", 111);
		map.put("offset", 322);
		map.put("order", "trtr");
		map.put("message", "this is a test");
		Object[] getBeanArgs = new Object[]{map,543543};
		Object hh = genService.invoke(null,"HelloRpcService", RpcUtils.DEFAULT_VERSION, "getBean", getBeanTypes, getBeanArgs);
		System.out.println(hh);
		client.stopService();
	}
}
