package com.linda.framework.rpc;

import java.io.Serializable;

/**
 * 服务名称，版本，实现类，以及服务启动时间
 * 用于存储zookeeper或者etcd，方便服务发现，json存储
 * @author lindezhi
 * 2016年3月9日 上午11:16:53
 */
public class RpcService implements Serializable {

	private static final long serialVersionUID = -4621627630242399962L;
	
	/**
	 * 服务名称，取impl类实现的接口class全名
	 */
	private String name;
	
	/**
	 * 服务版本，方便服务升级
	 */
	private String version;
	
	/**
	 * 服务实现类
	 */
	private String impl;
	
	/**
	 * 服务启动时间，用于监控与统计
	 */
	private long time;

	public RpcService() {

	}

	public RpcService(String name, String version) {
		this.name = name;
		this.version = version;
	}

	public RpcService(String name, String version, String impl) {
		this.name = name;
		this.version = version;
		this.impl = impl;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getImpl() {
		return impl;
	}

	public void setImpl(String impl) {
		this.impl = impl;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((impl == null) ? 0 : impl.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
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
		RpcService other = (RpcService) obj;
		if (impl == null) {
			if (other.impl != null)
				return false;
		} else if (!impl.equals(other.impl))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RpcMonitorBean [name=" + name + ", version=" + version + ", impl=" + impl + "]";
	}
}
