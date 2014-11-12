package com.linda.framework.rpc.oio;

import com.linda.framework.rpc.net.AbstractRpcConnector;

public class SimpleRpcOioWriter extends AbstractRpcOioWriter{

	@Override
	public boolean doSend(AbstractRpcConnector connector) {
		return super.exeSend(connector);
	}

}
