package com.linda.framework.rpc.server;

import java.util.List;

import com.linda.framework.rpc.filter.RpcFilter;
import com.linda.framework.rpc.filter.RpcStatFilter;
import com.linda.framework.rpc.generic.GenericService;
import com.linda.framework.rpc.generic.SimpleGenericService;
import com.linda.framework.rpc.monitor.RpcMonitorService;
import com.linda.framework.rpc.monitor.RpcMonitorServiceImpl;
import com.linda.framework.rpc.monitor.StatMonitor;
import com.linda.framework.rpc.net.AbstractRpcAcceptor;
import com.linda.framework.rpc.net.AbstractRpcNetworkBase;
import com.linda.framework.rpc.nio.AbstractRpcNioSelector;
import com.linda.framework.rpc.nio.RpcNioAcceptor;
import com.linda.framework.rpc.utils.RpcUtils;

public abstract class AbstractRpcServer extends AbstractRpcNetworkBase{

	private AbstractRpcAcceptor acceptor;
	private RpcServiceProvider provider = new RpcServiceProvider();
	private SimpleServerRemoteExecutor proxy = new SimpleServerRemoteExecutor();
	private RpcStatFilter statFilter = new RpcStatFilter();
	private int executorThreadCount = 20;//默认20
	
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
	public String getHost() {
		String host = super.getHost();
		if(host==null||host.equals("0.0.0.0")){
			List<String> iPs = RpcUtils.getLocalV4IPs();
			String chooseIP = RpcUtils.chooseIP(iPs);
			super.setHost(chooseIP);
		}
		return super.getHost();
	}

	@Override
	public void setHost(String host) {
		super.setHost(host);
	}

	@Override
	public void startService() {
		checkAcceptor();
		//监控filter
		statFilter.startService();
		
		this.addRpcFilter(statFilter);
		
		//默认添加监控
		this.addMonitor();
		//添加对泛型的支持
		this.addGenericSupport();
		
		acceptor.setHost(this.getHost());
		acceptor.setPort(this.getPort());
		provider.setExecutor(proxy);
		acceptor.addRpcCallListener(provider);
		acceptor.setExecutorThreadCount(executorThreadCount);
		acceptor.setExecutorSharable(false);
		acceptor.startService();
	}

	@Override
	public void stopService() {
		acceptor.stopService();
		proxy.stopService();
		provider.stopService();
		if(statFilter!=null){
			statFilter.stopService();
		}
	}

	public abstract AbstractRpcNioSelector getNioSelector();
	
	private void checkAcceptor(){
		if(acceptor==null){
			this.setAcceptor(new RpcNioAcceptor(getNioSelector()));
		}
	}
	
	private void addMonitor(){
		//通过filter监控访问次数
		this.register(RpcMonitorService.class, new RpcMonitorServiceImpl(proxy,statFilter));
	}
	
	/**
	 * 添加泛型的支持
	 */
	private void addGenericSupport(){
		this.register(GenericService.class, new SimpleGenericService(proxy));
	}

	public int getExecutorThreadCount() {
		return executorThreadCount;
	}

	public void setExecutorThreadCount(int executorThreadCount) {
		this.executorThreadCount = executorThreadCount;
	}
	
	public StatMonitor getStatMonitor(){
		return this.statFilter;
	}
}
