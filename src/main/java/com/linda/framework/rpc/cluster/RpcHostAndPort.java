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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		result = prime * result + port;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RpcHostAndPort other = (RpcHostAndPort) obj;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		if (port != other.port)
			return false;
		return true;
	}

}
