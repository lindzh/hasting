package com.linda.framework.rpc;

import java.io.Serializable;

public class RpcServiceBean implements Serializable {

	private static final long serialVersionUID = -1840492630641710459L;

	private Class interf;
	private String version;
	private Object bean;

	public RpcServiceBean(Class interf, Object bean, String version) {
		this.interf = interf;
		this.bean = bean;
		this.version = version;
	}

	public Class getInterf() {
		return interf;
	}

	public void setInterf(Class interf) {
		this.interf = interf;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Object getBean() {
		return bean;
	}

	public void setBean(Object bean) {
		this.bean = bean;
	}
}
