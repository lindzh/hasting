package com.linda.framework.rpc.aio;

import com.linda.framework.rpc.net.AbstractRpcConnector;
import com.linda.framework.rpc.net.AbstractRpcWriter;

/**
 * 
 * @author lindezhi
 *
 */
public class RpcAioWriter extends AbstractRpcWriter{

	@Override
	public boolean doSend(AbstractRpcConnector connector) {
		RpcAioConnector aioConnector = (RpcAioConnector)connector;
		aioConnector.exeSend();
		return true;
	}

}
