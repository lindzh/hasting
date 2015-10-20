package com.linda.framework.rpc.aio;

import com.linda.framework.rpc.net.AbstractRpcConnector;
import com.linda.framework.rpc.net.AbstractRpcWriter;

public class RpcAioConnector extends AbstractRpcConnector {
	
	public RpcAioConnector(){
		this(null);
	}

	public RpcAioConnector(AbstractRpcWriter rpcWriter) {
		super(rpcWriter);
	}

	@Override
	public void handleConnectorException(Exception e) {
		
	}

}
