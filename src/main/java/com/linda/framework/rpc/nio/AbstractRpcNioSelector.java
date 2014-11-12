package com.linda.framework.rpc.nio;

import com.linda.framework.rpc.Service;
import com.linda.framework.rpc.exception.RpcNetExceptionHandler;
import com.linda.framework.rpc.net.RpcOutputNofity;

public abstract class AbstractRpcNioSelector implements Service,RpcOutputNofity,RpcNetExceptionHandler{

	public abstract void register(RpcNioAcceptor acceptor);
	
	public abstract void unRegister(RpcNioAcceptor acceptor);
	
	public abstract void register(RpcNioConnector connector);
	
	public abstract void unRegister(RpcNioConnector connector);
	
}
