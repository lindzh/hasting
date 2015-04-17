package com.linda.framework.rpc.cluster;

import com.linda.framework.rpc.net.AbstractRpcAcceptor;
import com.linda.framework.rpc.net.RpcNetBase;
import com.linda.framework.rpc.net.RpcNetListener;
import com.linda.framework.rpc.server.SimpleRpcServer;

/**
 * 集群配置管理服务支持，统一管理rpc服务
 * @author lindezhi
 * 及时通知管理服务器提供的rpc和状态
 */
public abstract class RpcClusterServer extends SimpleRpcServer implements RpcNetListener{
	
	@Override
	public void register(Class<?> clazz, Object ifaceImpl) {
		super.register(clazz, ifaceImpl);
		this.doRegister(clazz, ifaceImpl);
	}

	@Override
	public void register(Class<?> clazz, Object ifaceImpl, String version) {
		super.register(clazz, ifaceImpl, version);
		this.doRegister(clazz, ifaceImpl, version);
	}
	
	protected abstract void doRegister(Class<?> clazz, Object ifaceImpl);
	
	protected abstract void doRegister(Class<?> clazz, Object ifaceImpl, String version);

	@Override
	public void setAcceptor(AbstractRpcAcceptor acceptor) {
		super.setAcceptor(acceptor);
		acceptor.addRpcNetListener(this);
	}
}
