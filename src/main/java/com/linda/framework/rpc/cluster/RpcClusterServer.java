package com.linda.framework.rpc.cluster;

import com.linda.framework.rpc.server.SimpleRpcServer;

public class RpcClusterServer extends SimpleRpcServer{

	@Override
	public void register(Class<?> clazz, Object ifaceImpl) {
		super.register(clazz, ifaceImpl);
	}

	@Override
	public void register(Class<?> clazz, Object ifaceImpl, String version) {
		super.register(clazz, ifaceImpl, version);
	}
}
