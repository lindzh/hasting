package com.linda.framework.rpc.aio;

import java.nio.channels.CompletionHandler;

public class RpcReadCompletionHandler<A> implements CompletionHandler<Integer,A> {

	@Override
	public void completed(Integer num, A att) {
		
	}

	@Override
	public void failed(Throwable e, A att) {
		
	}

}
