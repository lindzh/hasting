package com.linda.framework.rpc.net;

import com.linda.framework.rpc.Service;


public abstract class AbstractRpcAcceptor extends RpcNetBase implements Service {
	
	protected boolean stop = false;

	@Override
	public void startService() {
		super.startService();
		this.setExecutorSharable(false);
	}

	@Override
	public void stopService() {
		super.stopService();
	}
}
