package com.linda.framework.rpc.serializer;

/**
 * 对象，参数序列化
 * @author lindezhi
 * 2016年6月13日 下午4:24:26
 */
public interface RpcSerializer {

	/**
	 * 序列化
	 * @param obj
	 * @return
	 */
	public byte[] serialize(Object obj);
	
	/**
	 * 反序列化
	 * @param bytes
	 * @return
	 */
	public Object deserialize(byte[] bytes);
	
}
