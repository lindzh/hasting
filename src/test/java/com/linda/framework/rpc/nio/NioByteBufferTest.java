package com.linda.framework.rpc.nio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

public class NioByteBufferTest {
	
	static Logger logger = Logger.getLogger(NioByteBufferTest.class);
	
	public static void main(String[] args) {
		
		//ByteArrayInputStream bis = new ByteArrayInputStream(null);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		ByteBuffer buffer = ByteBuffer.allocate(256);
		buffer.putInt(21);
		buffer.putInt(43);
		infoBuffer(buffer);
		buffer.flip();
		infoBuffer(buffer);
		int int1 = buffer.getInt();
		int int2 = buffer.getInt();
		logger.info("int1:"+int1+" int2:"+int2);
	}
	
	public static void infoBuffer(ByteBuffer buffer){
		logger.info("position:"+buffer.position()+" limit:"+buffer.limit()+" mark:"+buffer.mark()+" capacity:"+buffer.capacity());
	}
	
}
