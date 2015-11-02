package com.linda.framework.rpc.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

import com.linda.framework.rpc.net.AbstractRpcConnector;
import com.linda.framework.rpc.net.AbstractRpcWriter;

public class RpcAioConnector extends AbstractRpcConnector {
	
	private AsynchronousSocketChannel channel;
	private ByteBuffer readBuf;
	private ByteBuffer writeBuf;
	
	private RpcReadCompletionHandler readHandler;
	private RpcWriteCompletionHandler writeHandler;
	
	public RpcAioConnector(AsynchronousSocketChannel channel){
		super(null);
		this.channel = channel;
	}
	
	public void setReadHandler(RpcReadCompletionHandler readHandler) {
		this.readHandler = readHandler;
	}

	public void setWriteHandler(RpcWriteCompletionHandler writeHandler) {
		this.writeHandler = writeHandler;
	}

	public void readCallback(int num){
		
	}

	public void writeCallback(int num){
		
	}
	
	public RpcAioConnector(AbstractRpcWriter rpcWriter) {
		super(rpcWriter);
	}

	@Override
	public void handleConnectorException(Exception e) {
		
	}
	
	public void handleFail(Throwable e, RpcAioConnector connector){
		
	}
}
