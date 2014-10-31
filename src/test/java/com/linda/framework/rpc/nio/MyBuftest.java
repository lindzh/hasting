package com.linda.framework.rpc.nio;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MyBuftest {
	
	public static void main(String[] args) throws IOException {
		RpcNioBuffer buf = new RpcNioBuffer();
		buf.writeInt(23);
		buf.writeLong(543534534);
		String str = "this is a test";
		byte[] bytes = str.getBytes();
		buf.write(bytes);
		buf.compact();
		int readInt = buf.readInt();
		long readLong = buf.readLong();
		
		byte[] readBytes = buf.readBytes();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		dos.writeInt(23);
		dos.writeLong(543534534);
		dos.write(bytes);
		byte[] byteArray = bos.toByteArray();
		System.out.println("buf:"+readBytes.length+"  "+byteArray.length);
		
	}

}
