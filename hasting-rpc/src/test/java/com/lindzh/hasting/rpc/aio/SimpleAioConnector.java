package com.lindzh.hasting.rpc.aio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

public class SimpleAioConnector<A> {

	private AsynchronousSocketChannel channel;
	private ByteBuffer readBuf;
	private ByteBuffer writeBuf;
	private SocketReadHandler<A> readHandler;
	private SocketWriteHandler<A> writeHandler;
	private A attachment;
	
	public void startService(){
		if(readBuf==null){
			readBuf = ByteBuffer.allocate(1024*16);
		}
		if(writeBuf==null){
			writeBuf = ByteBuffer.allocate(1024*16);
		}
		channel.read(readBuf, attachment, readHandler);
	}
	
	public void fireRead(int count){
		if(count<0){
			System.out.println("connection closed");
			this.stopService();
		}
		if(count>0){
			readBuf.flip();
			byte[] buf = new byte[count];
			readBuf.get(buf);
			System.out.println(new String(buf));
			writeBuf.put(buf);
			writeBuf.flip();
			this.channel.write(writeBuf, attachment, writeHandler);
			readBuf.clear();
			this.channel.read(readBuf, attachment, readHandler);
		}
	}
	
	public void fireWrite(int count){
		writeBuf.clear();
		System.out.println("write finish");
	}
	
	public void fireFailed(Throwable e){
		e.printStackTrace();
		this.stopService();
	}
	
	public void stopService(){
		System.out.println("haha close");
		try {
			this.channel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public SimpleAioConnector(AsynchronousSocketChannel channel){
		this.channel = channel;
	}

	public AsynchronousSocketChannel getChannel() {
		return channel;
	}

	public void setChannel(AsynchronousSocketChannel channel) {
		this.channel = channel;
	}

	public ByteBuffer getReadBuf() {
		return readBuf;
	}

	public void setReadBuf(ByteBuffer readBuf) {
		this.readBuf = readBuf;
	}

	public ByteBuffer getWriteBuf() {
		return writeBuf;
	}

	public void setWriteBuf(ByteBuffer writeBuf) {
		this.writeBuf = writeBuf;
	}

	public SocketReadHandler<A> getReadHandler() {
		return readHandler;
	}

	public void setReadHandler(SocketReadHandler<A> readHandler) {
		this.readHandler = readHandler;
	}

	public SocketWriteHandler<A> getWriteHandler() {
		return writeHandler;
	}

	public void setWriteHandler(SocketWriteHandler<A> writeHandler) {
		this.writeHandler = writeHandler;
	}

	public A getAttachment() {
		return attachment;
	}

	public void setAttachment(A attachment) {
		this.attachment = attachment;
	}
}
