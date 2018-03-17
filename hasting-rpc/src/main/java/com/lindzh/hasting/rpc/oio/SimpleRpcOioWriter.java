package com.lindzh.hasting.rpc.oio;

import com.lindzh.hasting.rpc.net.AbstractRpcConnector;

public class SimpleRpcOioWriter extends AbstractRpcOioWriter{

	@Override
	public boolean doSend(AbstractRpcConnector connector) {
		return super.exeSend(connector);
	}

}
