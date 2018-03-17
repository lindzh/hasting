package com.lindzh.hasting.cluster.serialize;

import com.lindzh.hasting.rpc.RemoteCall;
import com.lindzh.hasting.cluster.serializer.JSONSerializer;
import com.lindzh.hasting.rpc.serializer.RpcSerializer;

public class JSONSerializerTest extends AbstractSerializer {
	
	public static void main(String[] args) {
		SerializeTest test = new SerializeTest();
		RemoteCall call = test.getCall();
		RpcSerializer serializer = new JSONSerializer();
		long start = System.currentTimeMillis();
		byte[] bs = serializer.serialize(call);
		long end = System.currentTimeMillis();
		long cost = end-start;
		System.out.println("json serializer length:"+bs.length+" cost:"+cost);
	}

}
