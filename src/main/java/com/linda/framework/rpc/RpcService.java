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

	/**
	 * 所属应用,用于应用标记
	 */
	private String application;

	/**
	 * 应用部署分组,用于业务隔离
	 */
	private String group;

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

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		RpcService that = (RpcService) o;

		if (name != null ? !name.equals(that.name) : that.name != null) return false;
		if (version != null ? !version.equals(that.version) : that.version != null) return false;
		if (impl != null ? !impl.equals(that.impl) : that.impl != null) return false;
		if (application != null ? !application.equals(that.application) : that.application != null) return false;
		return group != null ? group.equals(that.group) : that.group == null;

	}

	@Override
	public int hashCode() {
		int result = name != null ? name.hashCode() : 0;
		result = 31 * result + (version != null ? version.hashCode() : 0);
		result = 31 * result + (impl != null ? impl.hashCode() : 0);
		result = 31 * result + (application != null ? application.hashCode() : 0);
		result = 31 * result + (group != null ? group.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "RpcService{" +
				"name='" + name + '\'' +
				", version='" + version + '\'' +
				", impl='" + impl + '\'' +
				", time=" + time +
				", application='" + application + '\'' +
				", group='" + group + '\'' +
				'}';
	}
}
