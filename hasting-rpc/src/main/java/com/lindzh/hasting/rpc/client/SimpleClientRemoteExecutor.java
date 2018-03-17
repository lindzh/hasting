package com.lindzh.hasting.rpc.client;

import com.lindzh.hasting.rpc.RemoteCall;
import com.lindzh.hasting.rpc.RemoteExecutor;
import com.lindzh.hasting.rpc.Service;
import com.lindzh.hasting.rpc.net.AbstractRpcConnector;
import com.lindzh.hasting.rpc.net.RpcCallListener;
import com.lindzh.hasting.rpc.oio.RpcOioConnector;

public class SimpleClientRemoteExecutor extends AbstractClientRemoteExecutor implements RemoteExecutor,RpcCallListener,Service{
	
	private AbstractRpcConnector connector;
	
	public SimpleClientRemoteExecutor(AbstractRpcConnector connector){
		super();
		connector.addRpcCallListener(this);
		this.connector = connector;
	}

	public AbstractRpcConnector getConnector() {
		return connector;
	}

	public void setConnector(RpcOioConnector connector) {
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
		return connector;
	}
}
