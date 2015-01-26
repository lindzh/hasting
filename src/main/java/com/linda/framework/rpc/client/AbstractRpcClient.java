package com.linda.framework.rpc.client;

import com.linda.framework.rpc.net.AbstractRpcConnector;
import com.linda.framework.rpc.net.AbstractRpcNetworkBase;

public abstract class AbstractRpcClient extends AbstractRpcNetworkBase{


	private SimpleClientRemoteProxy proxy = new SimpleClientRemoteProxy();
	
	protected Class<? extends AbstractRpcConnector> connectorClass;
	
	public abstract AbstractClientRemoteExecutor getRemoteExecutor();
	
	public Class<? extends AbstractRpcConnector> getConnectorClass() {
		return connectorClass;
	}

	public void setConnectorClass(Class<? extends AbstractRpcConnector> connectorClass) {
		this.connectorClass = connectorClass;
	}
	
	public abstract void initConnector();
	
	public <T> T register(Class<T> iface){
		return proxy.registerRemote(iface);
	}
	
	public <T> T register(Class<T> iface,String version){
		return proxy.registerRemote(iface, version);
	}
	
	@Override
	public void startService() {
		initConnector();
		proxy.setRemoteExecutor(getRemoteExecutor());
		proxy.startService();
	}
	
	@Override
	public void stopService() {
		proxy.stopService();
	}
	
}
