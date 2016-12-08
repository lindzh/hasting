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
import com.linda.framework.rpc.serializer.RpcSerializer;
import com.linda.framework.rpc.utils.RpcUtils;

/**
 * rpc服务端，需要启动tcp监听，然后接受客户端请求，提交到应用，执行，然后序列化执行结果，返回
 * @author lindezhi
 * 2015年6月14日 上午10:26:48
 */
public abstract class AbstractRpcServer extends AbstractRpcNetworkBase{

	/**
	 * 服务端监听器
	 */
	private AbstractRpcAcceptor acceptor;
	
	/**
	 * 服务端服务filter执行，提交给proxy
	 */
	private RpcServiceProvider provider = new RpcServiceProvider();
	
	/**
	 * 业务remote api注册与执行代理
	 */
	private SimpleServerRemoteExecutor proxy = new SimpleServerRemoteExecutor();
	
	/**
	 * 服务端性能统计
	 */
	private RpcStatFilter statFilter = new RpcStatFilter();
	
	/**
	 * 执行线程数量
	 */
	private int executorThreadCount = 20;//默认20

	/**
	 * 当前应用
	 */
	private String application;
	
	public void setAcceptor(AbstractRpcAcceptor acceptor){
		this.acceptor = acceptor;
	}
	
	/**
	 * 添加过滤器
	 * @param filter
	 */
	public void addRpcFilter(RpcFilter filter){
		provider.addRpcFilter(filter);
	}
	
	/**
	 * 注册为rpc服务
	 * @param clazz
	 * @param ifaceImpl
	 */
	public void register(Class<?> clazz,Object ifaceImpl){
		proxy.registerRemote(clazz, ifaceImpl,null);
	}
	
	/**
	 * 注册一个服务为rpc服务
	 * @param clazz
	 * @param ifaceImpl
	 * @param version
	 */
	public void register(Class<?> clazz,Object ifaceImpl,String version){
		proxy.registerRemote(clazz, ifaceImpl,version);
	}
	
	/**
	 * 获取当前服务器的ip，用于绑定监听
	 */
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

	/**
	 * 启动服务
	 */
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
		//初始化provider
		provider.setExecutor(proxy);
		//初始化accettor，并启动监听
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

	public int getTimeout() {
		return provider.getTimeout();
	}

	public void setTimeout(int timeout) {
		provider.setTimeout(timeout);
	}

	/**
	 * 这个提供nio支持的，非nio的不需要
	 * @return
	 */
	public abstract AbstractRpcNioSelector getNioSelector();
	
	/**
	 * 检查acceptor，如果没有就申明一个
	 */
	private void checkAcceptor(){
		if(acceptor==null){
			//默认使用nio
			this.setAcceptor(new RpcNioAcceptor(getNioSelector()));
		}
	}
	
	/**
	 * 添加系统默认的监听服务为rpc服务
	 */
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

	public RpcSerializer getSerializer() {
		return provider.getSerializer();
	}

	public void setSerializer(RpcSerializer serializer) {
		provider.setSerializer(serializer);
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}
}
