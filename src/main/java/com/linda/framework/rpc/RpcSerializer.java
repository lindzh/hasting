package com.linda.framework.rpc;

public interface RpcSerializer {

	public byte[] serialize(Object obj);
	
	public Object deserialize(byte[] bytes);
	
}
