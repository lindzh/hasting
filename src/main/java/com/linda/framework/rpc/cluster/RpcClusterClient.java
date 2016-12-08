package com.linda.framework.rpc.cluster;

import java.util.List;

import com.linda.framework.rpc.RpcService;
import com.linda.framework.rpc.client.AbstractClientRemoteExecutor;
import com.linda.framework.rpc.client.AbstractRpcClient;
import com.linda.framework.rpc.client.SimpleClientRemoteProxy;
import com.linda.framework.rpc.net.AbstractRpcConnector;
import com.linda.framework.rpc.utils.RpcUtils;

/**
 * 集群模式client
 * @author lindezhi
 * 2016年6月13日 下午4:20:23
 */
public class RpcClusterClient extends AbstractRpcClient{
	
	private SimpleClientRemoteProxy proxy;
	
	private AbstractRpcClusterClientExecutor executor;

	public void setRemoteExecutor(AbstractRpcClusterClientExecutor executor) {
		this.executor = executor;
	}

	@Override
	public <T> T register(Class<T> iface) {
		return this.register(iface, RpcUtils.DEFAULT_VERSION);
	}

	@Override
	public <T> T register(Class<T> iface, String version) {
		return this.register(iface, version,RpcUtils.DEFAULT_GROUP);
	}

	@Override
	public <T> T register(Class<T> iface, String version, String group) {
		this.checkProxy();
		if(version == null){
			version=RpcUtils.DEFAULT_VERSION;
		}

		if(group == null){
			group=RpcUtils.DEFAULT_GROUP;
		}

		return proxy.registerRemote(iface, version,group);
	}

	@Override
	public AbstractClientRemoteExecutor getRemoteExecutor() {
		return executor;
	}
	
	private AbstractRpcClusterClientExecutor getClusterClientExecutor(){
		return (AbstractRpcClusterClientExecutor)this.getRemoteExecutor();
	}
	
	private void checkProxy(){
		if(proxy==null){
			proxy = new SimpleClientRemoteProxy();
		}
	}

	@Override
	public void initConnector(int threadCount) {
		this.checkProxy();
		proxy.setRemoteExecutor(getRemoteExecutor());
	}

	@Override
	public Class<? extends AbstractRpcConnector> getConnectorClass() {
		return getClusterClientExecutor().getConnectorClass();
	}

	@Override
	public void setConnectorClass(Class<? extends AbstractRpcConnector> connectorClass) {
		getClusterClientExecutor().setConnectorClass(connectorClass);
	}

	//生成代理方便管理
	public List<RpcHostAndPort> getHostAndPorts() {
		return getClusterClientExecutor().getHostAndPorts();
	}

	//生成代理方便管理
	public List<RpcService> getServerService(RpcHostAndPort hostAndPort) {
		return getClusterClientExecutor().getServerService(hostAndPort);
	}
}
