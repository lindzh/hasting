package com.linda.framework.rpc.cluster;

import com.linda.framework.rpc.client.AbstractClientRemoteExecutor;
import com.linda.framework.rpc.client.AbstractRpcClient;
import com.linda.framework.rpc.client.SimpleClientRemoteProxy;
import com.linda.framework.rpc.net.AbstractRpcConnector;

public class RpcClusterClient extends AbstractRpcClient{
	
	private SimpleClientRemoteProxy proxy;
	
	private AbstractRpcClusterClientExecutor executor;
	
	public AbstractRpcClusterClientExecutor getExecutor() {
		return executor;
	}

	public void setExecutor(AbstractRpcClusterClientExecutor executor) {
		this.executor = executor;
	}

	@Override
	public <T> T register(Class<T> iface) {
		return proxy.registerRemote(iface);
	}

	@Override
	public <T> T register(Class<T> iface, String version) {
		return proxy.registerRemote(iface, version);
	}

	@Override
	public AbstractClientRemoteExecutor getRemoteExecutor() {
		return executor;
	}

	@Override
	public void initConnector(int threadCount) {
		if(proxy==null){
			proxy = new SimpleClientRemoteProxy();
		}
		proxy.setRemoteExecutor(executor);
	}

	@Override
	public Class<? extends AbstractRpcConnector> getConnectorClass() {
		return executor.getConnectorClass();
	}

	@Override
	public void setConnectorClass(Class<? extends AbstractRpcConnector> connectorClass) {
		executor.setConnectorClass(connectorClass);
	}
	
	
}
