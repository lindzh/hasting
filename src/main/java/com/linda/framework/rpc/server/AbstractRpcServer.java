package com.linda.framework.rpc.server;

import com.linda.framework.rpc.filter.RpcFilter;
import com.linda.framework.rpc.monitor.RpcMonitorService;
import com.linda.framework.rpc.monitor.RpcMonitorServiceImpl;
import com.linda.framework.rpc.net.AbstractRpcAcceptor;
import com.linda.framework.rpc.net.AbstractRpcNetworkBase;
import com.linda.framework.rpc.nio.AbstractRpcNioSelector;
import com.linda.framework.rpc.nio.RpcNioAcceptor;

public abstract class AbstractRpcServer extends AbstractRpcNetworkBase{

	private AbstractRpcAcceptor acceptor;
	private RpcServiceProvider provider = new RpcServiceProvider();
	private SimpleServerRemoteExecutor proxy = new SimpleServerRemoteExecutor();
	
	public void setAcceptor(AbstractRpcAcceptor acceptor){
		this.acceptor = acceptor;
	}
	
	public void addRpcFilter(RpcFilter filter){
		provider.addRpcFilter(filter);
	}
	
	public void register(Class<?> clazz,Object ifaceImpl){
		proxy.registerRemote(clazz, ifaceImpl,null);
	}
	
	public void register(Class<?> clazz,Object ifaceImpl,String version){
		proxy.registerRemote(clazz, ifaceImpl,version);
	}
	
	@Override
	public void setHost(String host) {
		super.setHost(host);
	}

	@Override
	public void setPort(int port) {
		super.setPort(port);
	}

	@Override
	public void startService() {
		checkAcceptor();
		this.addMonitor();
		acceptor.setHost(host);
		acceptor.setPort(port);
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

	public abstract AbstractRpcNioSelector getNioSelector();
	
	private void checkAcceptor(){
		if(acceptor==null){
			acceptor = new RpcNioAcceptor(getNioSelector());
		}
	}
	
	private void addMonitor(){
		this.register(RpcMonitorService.class, new RpcMonitorServiceImpl(proxy));
	}
}
