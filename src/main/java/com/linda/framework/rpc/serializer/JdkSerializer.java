package com.linda.framework.rpc.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.linda.framework.rpc.exception.RpcException;
import com.linda.framework.rpc.utils.NioUtils;

public class JdkSerializer implements RpcSerializer {

	@Override
	public byte[] serialize(Object obj) {
		try {
			ByteArrayOutputStream bis = new ByteArrayOutputStream();
			ObjectOutputStream stream = new ObjectOutputStream(bis);
			stream.writeObject(obj);
			stream.close();
			byte[] bytes = bis.toByteArray();
			//使用zip压缩，缩小网络包
			return NioUtils.zip(bytes);
		} catch (Exception e) {
			throw new RpcException(e);
		}
	}

	@Override
	public Object deserialize(byte[] bytes) {
		try {
			//使用zip解压缩
			byte[] unzip = NioUtils.unzip(bytes);
			ByteArrayInputStream bis = new ByteArrayInputStream(unzip);
			ObjectInputStream stream = new ObjectInputStream(bis);
			return stream.readObject();
		} catch (Exception e) {
			throw new RpcException(e);
		}
	}

}
