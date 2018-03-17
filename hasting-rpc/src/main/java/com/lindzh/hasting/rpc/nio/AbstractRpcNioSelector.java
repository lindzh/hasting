package com.lindzh.hasting.rpc.nio;

import java.util.LinkedList;
import java.util.List;

import com.lindzh.hasting.rpc.Service;
import com.lindzh.hasting.rpc.exception.RpcNetExceptionHandler;
import com.lindzh.hasting.rpc.net.RpcNetBase;
import com.lindzh.hasting.rpc.net.RpcNetListener;
import com.lindzh.hasting.rpc.net.RpcOutputNofity;

public abstract class AbstractRpcNioSelector implements Service,RpcOutputNofity,RpcNetExceptionHandler{

	public abstract void register(RpcNioAcceptor acceptor);
	
	public abstract void unRegister(RpcNioAcceptor acceptor);
	
	public abstract void register(RpcNioConnector connector);
	
	public abstract void unRegister(RpcNioConnector connector);
	
	public AbstractRpcNioSelector(){
		netListeners = new LinkedList<RpcNetListener>();
	}
	
	protected List<RpcNetListener> netListeners;
	
	public void addRpcNetListener(RpcNetListener listener){
		netListeners.add(listener);
	}
	
	public void fireNetListeners(RpcNetBase network,Exception e){
		for(RpcNetListener listener:netListeners){
			listener.onClose(network,e);
		}
	}
	
	
}
