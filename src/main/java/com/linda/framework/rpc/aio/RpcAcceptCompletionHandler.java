package com.linda.framework.rpc.aio;

import java.nio.channels.CompletionHandler;

public class RpcAcceptCompletionHandler<V,A> implements CompletionHandler<V,A> {

	@Override
	public void completed(V channel, A att) {
		
	}

	@Override
	public void failed(Throwable th, A att) {
		
	}

}
