package com.linda.framework.rpc.net;

import javax.net.ssl.SSLContext;

import com.linda.framework.rpc.Service;

public abstract class AbstractRpcNetworkBase implements Service{
	
	private String host;
	private int port;
	
	protected SSLContext sslContext;
	protected int sslMode;

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
	
	public SSLContext getSslContext() {
		return sslContext;
	}

	public void setSslContext(SSLContext sslContext) {
		this.sslContext = sslContext;
	}
	
	public int getSslMode() {
		return sslMode;
	}

	public void setSslMode(int sslMode) {
		this.sslMode = sslMode;
	}

}
