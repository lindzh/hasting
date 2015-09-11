package com.linda.framework.rpc;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

/**
 * 访问bean，方法，版本的封装，目前的版本version支持粒度为service
 * @author linda
 *
 */
public class RemoteCall implements Serializable{

	private static final long serialVersionUID = 2769999854843571360L;
	private String service;
	private String version;
	private String method;
	private Object[] args;
	private Map<String,Object> attachment;

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
	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
	public Map<String, Object> getAttachment() {
		return attachment;
	}

	public void setAttachment(Map<String, Object> attachment) {
		this.attachment = attachment;
	}

	@Override
	public String toString() {
		return "RemoteCall [service=" + service + ", version=" + version
				+ ", method=" + method + ", args=" + Arrays.toString(args)
				+ "]";
	}
}
