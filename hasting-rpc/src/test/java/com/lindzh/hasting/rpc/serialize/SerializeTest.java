package com.lindzh.hasting.rpc.serialize;

import com.lindzh.hasting.rpc.HelloRpcService;
import com.lindzh.hasting.rpc.RemoteCall;
import com.lindzh.hasting.rpc.TestBean;
import com.lindzh.hasting.rpc.serializer.JdkSerializer;

public class SerializeTest {
	
	public static void main(String[] args) {
		String service = HelloRpcService.class.getName();
		String method = "getBean";
		String version = "534543";
		TestBean testBean = new TestBean();
		testBean.setLimit(4);
		testBean.setMessage("ggggggggggggggggggggggggggggggggggggggggggggg");
		testBean.setOffset(43432);
		testBean.setOrder("645gdfghdfghdf");
		RemoteCall call = new RemoteCall(service, method);
		call.setArgs(new Object[]{testBean,654645});
		call.setVersion(version);
		JdkSerializer serializer = new JdkSerializer();
		byte[] bs = serializer.serialize(call);
		System.out.println(bs.length);
	}

}
