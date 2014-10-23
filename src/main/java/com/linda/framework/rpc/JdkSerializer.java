package com.linda.framework.rpc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class JdkSerializer implements RpcSerializer {

	@Override
	public byte[] serialize(Object obj) {
		try {
			ByteArrayOutputStream bis = new ByteArrayOutputStream();
			ObjectOutputStream stream = new ObjectOutputStream(bis);
			stream.writeObject(obj);
			stream.close();
			return bis.toByteArray();
		} catch (Exception e) {
			throw new RpcException(e);
		}
	}

	@Override
	public Object deserialize(byte[] bytes) {
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			ObjectInputStream stream = new ObjectInputStream(bis);
			return stream.readObject();
		} catch (Exception e) {
			throw new RpcException(e);
		}
	}

}
