package com.linda.framework.rpc.client;

import com.linda.framework.rpc.RemoteCall;
import com.linda.framework.rpc.RemoteExecutor;
import com.linda.framework.rpc.Service;
import com.linda.framework.rpc.exception.RpcException;
import com.linda.framework.rpc.net.AbstractRpcConnector;
import com.linda.framework.rpc.net.RpcCallListener;
import com.linda.framework.rpc.net.RpcMultiConnector;

public class MultiClientRemoteExecutor extends AbstractClientRemoteExecutor implements RemoteExecutor,RpcCallListener,Service{

	private RpcMultiConnector connector;
	
	public MultiClientRemoteExecutor(RpcMultiConnector connector){
		super();
		connector.addRpcCallListener(this);
		this.connector = connector;
	}
	
	@Override
	public void startService() {
		connector.startService();
	}

	@Override
	public void stopService() {
		connector.stopService();
	}

	@Override
	public AbstractRpcConnector getRpcConnector(RemoteCall call) {
		AbstractRpcConnector resource = connector.getResource();
		if(resource==null){
			throw new RpcException("connection lost");
		}
		return resource;
	}


}
