package com.linda.framework.rpc.server;

import com.linda.framework.rpc.filter.RpcFilter;
import com.linda.framework.rpc.net.AbstractRpcAcceptor;
import com.linda.framework.rpc.net.AbstractRpcNetworkBase;
import com.linda.framework.rpc.nio.RpcNioAcceptor;

public class RpcServer extends AbstractRpcNetworkBase{

	private AbstractRpcAcceptor acceptor = new RpcNioAcceptor();
	private RpcServiceProvider provider = new RpcServiceProvider();
	private SimpleServerRemoteExecutor proxy = new SimpleServerRemoteExecutor();
	
	public void setAcceptor(AbstractRpcAcceptor acceptor){
		this.acceptor = acceptor;
	}
	
	public void addRpcFilter(RpcFilter filter){
		provider.addRpcFilter(filter);
	}
	
	public void register(Class<?> iface,Object obj){
		proxy.registerRemote(iface, obj);
	}
	
	@Override
	public void setHost(String host) {
		super.setHost(host);
		acceptor.setHost(host);
	}

	@Override
	public void setPort(int port) {
		super.setPort(port);
		acceptor.setPort(port);
	}

	@Override
	public void startService() {
		provider.setExecutor(proxy);
		acceptor.addRpcCallListener(provider);
		acceptor.startService();
	}

	@Override
	public void stopService() {
		acceptor.stopService();
		proxy.stopService();
		provider.stopService();
	}

}
