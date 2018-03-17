package com.lindzh.hasting.rpc.net;

import com.lindzh.hasting.rpc.Service;
import com.lindzh.hasting.rpc.exception.RpcException;


public abstract class AbstractRpcAcceptor extends RpcNetBase implements Service {
	
	protected boolean stop = false;

	@Override
	public void startService() {
		super.startService();
		this.setExecutorSharable(false);
	}

	@Override
	public void stopService() {
		this.fireCloseNetListeners(new RpcException("acceptor close"));
		super.stopService();
	}
}
