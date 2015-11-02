package com.linda.framework.rpc.aio;

import java.nio.channels.CompletionHandler;

public class RpcReadCompletionHandler implements CompletionHandler<Integer,RpcAioConnector> {

	@Override
	public void completed(Integer num, RpcAioConnector connector) {
		if(num!=null){
			connector.readCallback(num);
		}
	}

	@Override
	public void failed(Throwable e, RpcAioConnector connector) {
		connector.handleFail(e, connector);
	}

}
