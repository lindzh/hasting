package com.linda.framework.rpc.serializer;

public interface RpcSerializer {

	public byte[] serialize(Object obj);
	
	public Object deserialize(byte[] bytes);
	
}
