package com.linda.framework.rpc.nio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

public class NioByteBufferTest {
	
	static Logger logger = Logger.getLogger(NioByteBufferTest.class);
	
	public static void testBuf() throws IOException{
		String str = "this is a test";
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		WritableByteChannel writeChannel = Channels.newChannel(bos);
		ByteBuffer buffer = ByteBuffer.allocate(256);
		buffer.putInt(21);
		buffer.putInt(43);
		byte[] bytes = str.getBytes();
		buffer.putInt(bytes.length);
		buffer.put(bytes);
		infoBuffer(buffer);
		buffer.flip();
		buffer.position(4);
		int write = writeChannel.write(buffer);
		infoBuffer(buffer);
		byte[] dest = bos.toByteArray();
		logger.info("dest len:"+dest.length);
		ByteArrayInputStream bis = new ByteArrayInputStream(dest);
		ReadableByteChannel readChannel = Channels.newChannel(bis);
		infoBuffer(buffer);
		buffer.clear();
		int read = readChannel.read(buffer);
		infoBuffer(buffer);
		buffer.flip();
		infoBuffer(buffer);
		int int1 = buffer.getInt();
		int int2 = buffer.getInt();
		int len = buffer.getInt();
		byte[] sss = new byte[len];
		buffer.get(sss, 0, len);
		logger.info("int1:"+int1+" int2:"+int2+" len:"+len);
		logger.info("str:"+new String(sss));
	}
	
	public static void main(String[] args) throws IOException {
		LinkedList<String> list = new LinkedList<String>();
		list.offer("123");
		list.offer("234");
		String peek = list.peek();
		String peek2 = list.peek();
		logger.info("peak:"+peek+" "+peek2);
	}
	
	public static void infoBuffer(ByteBuffer buffer){
		logger.info("position:"+buffer.position()+" limit:"+buffer.limit()+" mark:"+buffer.mark()+" capacity:"+buffer.capacity());
	}
	
}
