package com.linda.framework.rpc.cluster;

import com.linda.framework.rpc.net.AbstractRpcNetworkBase;

public class RpcHostAndPort extends AbstractRpcNetworkBase{
	
	public RpcHostAndPort(String host,int port){
		this.host = host;
		this.port = port;
	}

	@Override
	public void startService() {
		
	}

	@Override
	public void stopService() {
		
	}

	@Override
	public String toString() {
		return host + ":" + port;
	}
	
}
