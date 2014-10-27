package com.linda.framework.rpc.net;

import com.linda.framework.rpc.Service;


public abstract class AbstractRpcAcceptor extends RpcNetBase implements Service {
	
	protected String host;
	protected int port;
	protected boolean stop = false;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
}
