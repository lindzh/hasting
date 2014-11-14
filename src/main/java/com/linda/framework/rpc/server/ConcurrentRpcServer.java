package com.linda.framework.rpc.server;

import com.linda.framework.rpc.nio.AbstractRpcNioSelector;
import com.linda.framework.rpc.nio.ConcurrentRpcNioSelector;

public class ConcurrentRpcServer extends AbstractRpcServer{

	private AbstractRpcNioSelector nioSelector;
	
	@Override
	public AbstractRpcNioSelector getNioSelector() {
		if(nioSelector==null){
			nioSelector = new ConcurrentRpcNioSelector();
		}
		return nioSelector;
	}

}
