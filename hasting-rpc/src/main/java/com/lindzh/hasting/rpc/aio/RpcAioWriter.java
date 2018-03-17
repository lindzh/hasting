package com.lindzh.hasting.rpc.aio;

import com.lindzh.hasting.rpc.net.AbstractRpcConnector;
import com.lindzh.hasting.rpc.net.AbstractRpcWriter;

/**
 * 
 * @author lindezhi
 *
 */
public class RpcAioWriter extends AbstractRpcWriter {
	
	public RpcAioWriter(){
		super();
	}

	/**
	 * 使用channel执行发送
	 */
	@Override
	public boolean doSend(AbstractRpcConnector connector) {
		RpcAioConnector aioConnector = (RpcAioConnector)connector;
		aioConnector.exeSend();
		return true;
	}

}
