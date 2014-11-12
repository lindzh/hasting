package com.linda.framework.rpc.client;

import com.linda.framework.rpc.net.AbstractRpcConnector;
import com.linda.framework.rpc.net.AbstractRpcNetworkBase;
import com.linda.framework.rpc.nio.ConcurrentRpcNioSelector;
import com.linda.framework.rpc.nio.RpcNioConnector;
import com.linda.framework.rpc.nio.SimpleRpcNioSelector;

public class RpcClient extends AbstractRpcNetworkBase{

	private AbstractRpcConnector connector;
	private SimpleClientRemoteExecutor executor;
	private SimpleClientRemoteProxy proxy = new SimpleClientRemoteProxy();;
	
	public <T> T register(Class<T> iface){
		return proxy.registerRemote(iface);
	}
	
	@Override
	public void setHost(String host) {
		checkConnector();
		super.setHost(host);
		connector.setHost(host);
	}

	@Override
	public void setPort(int port) {
		checkConnector();
		super.setPort(port);
		connector.setPort(port);
	}

	@Override
	public void startService() {
		executor = new SimpleClientRemoteExecutor(connector);
		proxy.setRemoteExecutor(executor);
		proxy.startService();
	}

	@Override
	public void stopService() {
		proxy.stopService();
	}
	
	private void checkConnector(){
		if(connector==null){
			SimpleRpcNioSelector selector = new SimpleRpcNioSelector();
			connector = new RpcNioConnector(selector);
		}
	}
}
