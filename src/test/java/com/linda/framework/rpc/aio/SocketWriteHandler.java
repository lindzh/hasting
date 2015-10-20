package com.linda.framework.rpc.aio;

import java.nio.channels.CompletionHandler;

public class SocketWriteHandler<A> implements CompletionHandler<Integer,A> {

	@Override
	public void completed(Integer result, A attachment) {
		
	}

	@Override
	public void failed(Throwable exc, A attachment) {
		
	}
}
