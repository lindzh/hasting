package com.linda.framework.rpc;

public interface RpcFilter {
	
	public void doFilter(RpcObject rpc,RemoteCall call,RpcSend sender,RpcFilterChain chain);

}
