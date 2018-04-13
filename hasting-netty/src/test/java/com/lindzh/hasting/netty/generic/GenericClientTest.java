package com.lindzh.hasting.netty.generic;

import java.util.HashMap;

import com.lindzh.hasting.rpc.client.SimpleRpcClient;
import com.lindzh.hasting.rpc.generic.GenericService;
import com.lindzh.hasting.netty.RpcNettyConnector;
import com.lindzh.hasting.rpc.utils.RpcUtils;

public class GenericClientTest {
	
	public static void main(String[] a) {
		SimpleRpcClient client = new SimpleRpcClient();
		client.setConnectorClass(RpcNettyConnector.class);
		client.setHost("127.0.0.1");
		client.setPort(4445);
		client.startService();
		GenericService service = client.register(GenericService.class);
		
		String[] getBeanTypes = new String[]{"com.lindzh.hasting.netty.TestBean","int"};
		HashMap<String,Object> map = new HashMap<String,Object>();
		map.put("limit", 111);
		map.put("offset", 322);
		map.put("order", "trtr");
		map.put("message", "this is a test");
		Object[] getBeanArgs = new Object[]{map,543543};
		Object hh = service.invoke(null,"com.lindzh.hasting.netty.HelloRpcService", RpcUtils.DEFAULT_VERSION, "getBean", getBeanTypes, getBeanArgs);
		System.out.println(hh);
		
		String[] argTypes = new String[]{"java.lang.String","int"};
		Object[] args = new Object[]{"hello,this is generic",543543};
		service.invoke(null,"com.lindzh.hasting.netty.HelloRpcService", RpcUtils.DEFAULT_VERSION, "sayHello", argTypes, args);

	}

}
