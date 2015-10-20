package com.linda.framework.rpc.aio;

import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class SocketReadHandler<A> implements CompletionHandler<Integer,A>{

	// read buf future //顺序
	// write buf future cache 顺序
	
	@Override
	public void completed(Integer result, A attachment) {
		AsynchronousSocketChannel socket = (AsynchronousSocketChannel)attachment; 
	}

	@Override
	public void failed(Throwable exc, A attachment) {
		AsynchronousSocketChannel socket = (AsynchronousSocketChannel)attachment;
		
	}
}
