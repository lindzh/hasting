package com.lindzh.hasting.rpc.aio;

import java.nio.channels.CompletionHandler;

/**
 * 
 * @author lindezhi
 * 2015年6月13日 下午4:23:09
 */
public class RpcWriteCompletionHandler implements CompletionHandler<Integer,RpcAioConnector> {

	/**
	 * 写发送成功回调
	 */
	@Override
	public void completed(Integer num, RpcAioConnector connector) {
		if(num!=null){
			connector.writeCallback(num);
		}
	}

	/**
	 * 写失败回调，如网络异常
	 */
	@Override
	public void failed(Throwable e, RpcAioConnector connector) {
		connector.handleFail(e, connector);
	}

}
