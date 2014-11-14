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
			SimpleRpcNioSelector nioSelector = new SimpleRpcNioSelector();
			SimpleRpcOioWriter writer = new SimpleRpcOioWriter();
			if(connectorClass==null){
				connectorClass = RpcNioConnector.class;
			}
			connector = RpcUtils.createRpcConnector(nioSelector, writer, connectorClass);
		}
	}

	@Override
	public AbstractClientRemoteExecutor getRemoteExecutor() {
		return executor;
	}

	@Override
	public void initConnector() {
		checkConnector();
		connector.setHost(host);
		connector.setPort(port);
		executor = new SimpleClientRemoteExecutor(connector);
	}
}
