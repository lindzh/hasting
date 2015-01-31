package com.linda.framework.rpc.client;

import com.linda.framework.rpc.RemoteCall;
import com.linda.framework.rpc.RemoteExecutor;
import com.linda.framework.rpc.Service;
import com.linda.framework.rpc.net.AbstractRpcConnector;
import com.linda.framework.rpc.net.RpcCallListener;
import com.linda.framework.rpc.oio.RpcOioConnector;

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
