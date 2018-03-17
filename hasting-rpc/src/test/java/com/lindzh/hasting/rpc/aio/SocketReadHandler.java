package com.lindzh.hasting.rpc.aio;

import java.nio.channels.CompletionHandler;

public class SocketReadHandler<A> implements CompletionHandler<Integer,A>{

	@Override
	public void completed(Integer result, A attachment) {
		SimpleAioConnector connector = (SimpleAioConnector)attachment;
		connector.fireRead(result);
//		connector.getChannel().read(connector.getReadBuf(), attachment, connector.getReadHandler());
	}

	@Override
	public void failed(Throwable exc, A attachment) {
		SimpleAioConnector connector = (SimpleAioConnector)attachment;
		connector.fireFailed(exc);
	}
}
