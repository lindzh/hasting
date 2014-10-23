package com.linda.framework.rpc;

public interface RpcFilterChain {
	
	public void nextFilter(RpcObject rpc,RemoteCall call,RpcSend sender);
	
	public void addRpcFilter(RpcFilter filter);
	
	public void startFilter(RpcObject rpc,RemoteCall call,RpcSend sender);
	
}
