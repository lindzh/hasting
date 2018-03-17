package com.lindzh.hasting.rpc.client;

import com.lindzh.hasting.rpc.net.AbstractRpcConnector;
import com.lindzh.hasting.rpc.utils.RpcUtils;

public class SimpleRpcClient extends AbstractRpcClient{

	private AbstractRpcConnector connector;
	private AbstractClientRemoteExecutor executor;

	private void checkConnector(){
		if(connector==null){
			connector = RpcUtils.createConnector(connectorClass);
		}
	}

	@Override
	public AbstractClientRemoteExecutor getRemoteExecutor() {
		return executor;
	}

	@Override
	public void initConnector(int threadCount) {
		checkConnector();
		connector.setHost(this.getHost());
		connector.setPort(this.getPort());
		connector.setExecutorThreadCount(threadCount);
		executor = new SimpleClientRemoteExecutor(connector);
	}
}
