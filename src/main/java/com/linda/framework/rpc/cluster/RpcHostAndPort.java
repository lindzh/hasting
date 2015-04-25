package com.linda.framework.rpc.cluster;

public class RpcHostAndPort {

	private String host;
	private int port;

	public RpcHostAndPort() {

	}

	public RpcHostAndPort(String host, int port) {
		this.host = host;
		this.port = port;
	}

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

	@Override
	public String toString() {
		return host + ":" + port;
	}

}
