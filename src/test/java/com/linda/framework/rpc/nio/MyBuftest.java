package com.linda.framework.rpc.nio;

public class MyBuftest {
	
	public static void main(String[] args) {
		RpcNioBuffer buf = new RpcNioBuffer();
		buf.writeInt(23);
		String str = "this is a test";
		byte[] bytes = str.getBytes();
		buf.write(bytes);
		buf.readInt();
		buf.compact();
		byte[] readBytes = buf.readBytes(bytes.length);
		String string = new String(readBytes);
		System.out.println(string);
	}

}
