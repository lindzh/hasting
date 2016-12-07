package com.linda.framework.rpc.cluster;

/**
 * 服务提供者provider，用于注册存储
 * @author lindezhi
 * 2015年6月13日 下午2:59:18
 */
public class RpcHostAndPort {
	
	/**
	 * provider host
	 */
	private String host;
	/**
	 * provider port
	 */
	private int port;
	/**
	 * provider启动时间
	 */
	private long time;

	/**
	 * token校验,用于验证客户端调用服务器service必须从注册中心拉取到
	 */
	private String token;

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

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		RpcHostAndPort that = (RpcHostAndPort) o;

		if (port != that.port) return false;
		if (host != null ? !host.equals(that.host) : that.host != null) return false;
		return token != null ? token.equals(that.token) : that.token == null;
	}

	@Override
	public int hashCode() {
		int result = host != null ? host.hashCode() : 0;
		result = 31 * result + port;
		result = 31 * result + (token != null ? token.hashCode() : 0);
		return result;
	}
}
