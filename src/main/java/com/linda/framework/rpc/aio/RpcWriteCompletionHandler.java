package com.linda.framework.rpc.aio;

import java.nio.channels.CompletionHandler;

public class RpcWriteCompletionHandler implements CompletionHandler<Integer,RpcAioConnector> {

	@Override
	public void completed(Integer num, RpcAioConnector connector) {
		if(num!=null){
			connector.writeCallback(num);
		}
	}

	@Override
	public void failed(Throwable e, RpcAioConnector connector) {
		connector.handleFail(e, connector);
	}

}
