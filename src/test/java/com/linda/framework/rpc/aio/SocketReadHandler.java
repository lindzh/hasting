package com.linda.framework.rpc.aio;

import java.nio.channels.CompletionHandler;

public class SocketReadHandler<A> implements CompletionHandler<Integer,A>{

	@Override
	public void completed(Integer result, A attachment) {
		SimpleAioConnector connector = (SimpleAioConnector)attachment;
		connector.fireRead(result);
	}

	@Override
	public void failed(Throwable exc, A attachment) {
		SimpleAioConnector connector = (SimpleAioConnector)attachment;
		connector.fireFailed(exc);
	}
}
