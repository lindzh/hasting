package com.lindzh.hasting.cluster.serializer;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.lindzh.hasting.cluster.JSONUtils;
import com.lindzh.hasting.rpc.exception.RpcException;
import com.lindzh.hasting.rpc.serializer.RpcSerializer;

public class JSONSerializer implements RpcSerializer{

	@Override
	public byte[] serialize(Object obj) {
		String className = obj.getClass().getName();
		HashMap<String,String> map = new HashMap<String,String>();
		map.put("className", className);
		long start = System.currentTimeMillis();
		String json = JSONUtils.toJSON(obj);
		long end = System.currentTimeMillis();
		System.out.println("cost1:"+(end-start));
		map.put("jsonContent", json);
		try {
			start = System.currentTimeMillis();
			byte[] bytes = JSONUtils.toJSON(map).getBytes("utf-8");
			end = System.currentTimeMillis();
			System.out.println("cost2:"+(end-start));
			return bytes;
		} catch (UnsupportedEncodingException e) {
			throw new RpcException(e);
		}
	}

	@Override
	public Object deserialize(byte[] bytes) {
		try {
			String json = new String(bytes,"utf-8");
			Map map = JSONUtils.fromJSON(json, Map.class);
			String className = (String)map.get("className");
			String jsonContent = (String)map.get("jsonContent");
			try {
				Class<?> clazz = Class.forName(className);
				return JSONUtils.fromJSON(jsonContent, clazz);
			} catch (ClassNotFoundException e) {
				throw new RpcException(e);
			}
		} catch (UnsupportedEncodingException e) {
			throw new RpcException(e);
		}
	}
}
