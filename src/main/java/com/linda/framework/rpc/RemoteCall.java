package com.linda.framework.rpc;

import java.io.Serializable;
import java.util.Arrays;

public class RemoteCall implements Serializable{

	private static final long serialVersionUID = 1267772348012469199L;
	
	private String service;
	private String method;
	private Object[] args;

	public RemoteCall(String service,String method) {
		this.service = service;
		this.method = method;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	@Override
	public String toString() {
		return "RemoteCall [service=" + service + ", method=" + method
				+ ", args=" + Arrays.toString(args) + "]";
	}
}
