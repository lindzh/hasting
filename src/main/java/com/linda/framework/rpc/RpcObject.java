package com.linda.framework.rpc;

import java.util.concurrent.ConcurrentHashMap;

import com.linda.framework.rpc.RpcUtils.RpcType;

public class RpcObject {
	
	private RpcType type;
	private long threadId;
	private int index;
	private int length;
	private byte[] data;
	
	private String host;
	private int port;
	private ConcurrentHashMap<String,Object> rpcContext;
	
	public RpcObject(){
		
	}
	
	public RpcObject(int type,int index,int len,byte[] data){
		this.type = RpcType.getByType(type);
		this.index = index;
		this.length = len;
		this.data = data;
		this.threadId = Thread.currentThread().getId();
	}

	public RpcType getType() {
		return type;
	}

	public void setType(RpcType type) {
		this.type = type;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public long getThreadId() {
		return threadId;
	}

	public void setThreadId(long threadId) {
		this.threadId = threadId;
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

	public ConcurrentHashMap<String, Object> getRpcContext() {
		return rpcContext;
	}

	public void setRpcContext(ConcurrentHashMap<String, Object> rpcContext) {
		this.rpcContext = rpcContext;
	}
}
