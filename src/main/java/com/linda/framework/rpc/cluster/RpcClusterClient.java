package com.linda.framework.rpc.cluster;

import java.util.List;

import com.linda.framework.rpc.RpcService;
import com.linda.framework.rpc.client.AbstractClientRemoteExecutor;
import com.linda.framework.rpc.client.AbstractRpcClient;
import com.linda.framework.rpc.client.SimpleClientRemoteProxy;
import com.linda.framework.rpc.net.AbstractRpcConnector;

public class RpcClusterClient extends AbstractRpcClient{
	
	private SimpleClientRemoteProxy proxy;
	
	private AbstractRpcClusterClientExecutor executor;

	public void setRemoteExecutor(AbstractRpcClusterClientExecutor executor) {
		this.executor = executor;
	}

	@Override
	public <T> T register(Class<T> iface) {
		this.checkProxy();
		return proxy.registerRemote(iface);
	}

	@Override
	public <T> T register(Class<T> iface, String version) {
		this.checkProxy();
		return proxy.registerRemote(iface, version);
	}

	@Override
	public AbstractClientRemoteExecutor getRemoteExecutor() {
		return executor;
	}
	
	private void checkProxy(){
		if(proxy==null){
			proxy = new SimpleClientRemoteProxy();
		}
	}

	@Override
	public void initConnector(int threadCount) {
		this.checkProxy();
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

	//生成代理方便管理
	public List<RpcHostAndPort> getHostAndPorts() {
		return executor.getHostAndPorts();
	}

	//生成代理方便管理
	public List<RpcService> getServerService(RpcHostAndPort hostAndPort) {
		return executor.getServerService(hostAndPort);
	}
}
