package com.lindzh.hasting.rpc.aio;

import java.nio.channels.CompletionHandler;

public class SocketAcceptHandler<V,A> implements CompletionHandler<V,A>{

	@Override
	public void completed(V result, A attachment) {
		
	}

	@Override
	public void failed(Throwable exc, A attachment) {
		
	}

}
