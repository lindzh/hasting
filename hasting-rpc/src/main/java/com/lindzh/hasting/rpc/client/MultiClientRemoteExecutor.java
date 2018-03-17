package com.lindzh.hasting.rpc.client;

import com.lindzh.hasting.rpc.RemoteCall;
import com.lindzh.hasting.rpc.RemoteExecutor;
import com.lindzh.hasting.rpc.Service;
import com.lindzh.hasting.rpc.exception.RpcException;
import com.lindzh.hasting.rpc.net.AbstractRpcConnector;
import com.lindzh.hasting.rpc.net.RpcCallListener;
import com.lindzh.hasting.rpc.net.RpcMultiConnector;

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
