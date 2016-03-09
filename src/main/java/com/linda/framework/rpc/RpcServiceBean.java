package com.linda.framework.rpc;

import java.io.Serializable;

/**
 * rpc服务接口版本，实现对象
 * @author lindezhi
 * 2016年3月9日 上午11:15:00
 */
public class RpcServiceBean implements Serializable {

	private static final long serialVersionUID = -1840492630641710459L;

	/**
	 * remote接口class
	 */
	private Class interf;
	
	/**
	 * remote接口class版本
	 */
	private String version;
	
	/**
	 * interf实现对象
	 */
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
