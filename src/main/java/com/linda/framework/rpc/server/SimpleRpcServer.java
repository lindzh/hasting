package com.linda.framework.rpc.server;

import com.linda.framework.rpc.nio.AbstractRpcNioSelector;
import com.linda.framework.rpc.nio.SimpleRpcNioSelector;

public class SimpleRpcServer extends AbstractRpcServer{
	
	private AbstractRpcNioSelector nioSelector;

	@Override
	public AbstractRpcNioSelector getNioSelector() {
		if(nioSelector==null){
			nioSelector = new SimpleRpcNioSelector();
		}
		return nioSelector;
	}


}
