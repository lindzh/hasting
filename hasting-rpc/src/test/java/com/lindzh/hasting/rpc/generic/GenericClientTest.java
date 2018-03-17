package com.lindzh.hasting.rpc.generic;

import java.util.HashMap;

import com.lindzh.hasting.rpc.RpcContext;
import com.lindzh.hasting.rpc.client.SimpleRpcClient;
import com.lindzh.hasting.rpc.utils.RpcUtils;

public class GenericClientTest {

	public static void main(String[] a) {
		SimpleRpcClient client = new SimpleRpcClient();
		client.setHost("127.0.0.1");
		client.setPort(4445);
		client.startService();
		GenericService service = client.register(GenericService.class);

		String[] getBeanTypes = new String[] {
				"TestBean", "int" };
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("limit", 111);
		map.put("offset", 322);
		map.put("order", "trtr");
		map.put("message", "this is a test");
		Object[] getBeanArgs = new Object[] { map, 543543 };
		RpcContext.getContext().setAttachment("myhaha", "myattachment value");
		Object hh = service.invoke("aapp","HelloRpcService",
				RpcUtils.DEFAULT_VERSION, "getBean", getBeanTypes, getBeanArgs);
		RpcContext.getContext().clear();
		System.out.println("getBean result:" + hh);

		String[] argTypes = new String[] { "java.lang.String", "int" };
		Object[] args = new Object[] { "hello,this is linda", 543543 };
		RpcContext.getContext().setAttachment("myattachment", "myhaha value");
		Object invoke = service.invoke("aapp",
				"HelloRpcService",
				RpcUtils.DEFAULT_VERSION, "sayHello", argTypes, args);
		RpcContext.getContext().clear();
		System.out.println("sayHello result:" + invoke);
		System.out.println("---------------");
		client.stopService();
	}

}
