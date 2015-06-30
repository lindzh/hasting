package com.linda.framework.rpc.client;

import com.linda.framework.rpc.net.AbstractRpcConnector;
import com.linda.framework.rpc.nio.RpcNioConnector;
import com.linda.framework.rpc.nio.SimpleRpcNioSelector;
import com.linda.framework.rpc.oio.SimpleRpcOioWriter;
import com.linda.framework.rpc.utils.RpcUtils;

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
