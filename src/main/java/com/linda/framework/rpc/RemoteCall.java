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
	
	/**
	 * 服务名称 classname
	 */
	private String service;
	
	/**
	 * 服务版本
	 */
	private String version;
	
	/**
	 * 方法
	 */
	private String method;
	
	/**
	 * 方法参数
	 */
	private Object[] args;
	
	/**
	 * 上下文，用户传递相关参数，使用案例如分布式环境下log跟踪，同一个客户端请求，经过的所有服务器处理flag一致，方便搜索与跟踪
	 */
	private Map<String,Object> attachment;

	public RemoteCall(){

	}

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
