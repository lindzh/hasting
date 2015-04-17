package com.linda.framework.rpc;

import java.io.Serializable;

public class RpcService implements Serializable{
	
	private static final long serialVersionUID = -4621627630242399962L;
	
	public RpcService(String name,String version){
		this.name = name;
		this.version = version;
	}
	
	public RpcService(String name,String version,String impl){
		this.name = name;
		this.version = version;
		this.impl = impl;
	}
	
	private String name;
	private String version;
	private String impl;

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
	public String toString() {
		return "RpcMonitorBean [name=" + name + ", version=" + version
				+ ", impl=" + impl + "]";
	}
}
