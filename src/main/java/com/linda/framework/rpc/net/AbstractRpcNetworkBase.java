package com.linda.framework.rpc.net;

import javax.net.ssl.SSLContext;

import com.linda.framework.rpc.Service;

/**
 * 网络父类，服务端和客户端都需要继承
 * @author lindezhi
 * 2015年6月14日 上午10:24:26
 */
public abstract class AbstractRpcNetworkBase implements Service{
	/**
	 * ip服务端表示绑定ip，客户端表示连接到服务器的监听ip
	 */
	private String host;
	
	/**
	 * 端口服务端表示绑定端口，客户端表示连接到服务端的监听端口
	 */
	private int port;
	
	/**
	 * 启用SSL的选项，一般不用
	 */
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
