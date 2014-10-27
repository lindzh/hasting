package com.linda.framework.rpc.filter;

import com.linda.framework.rpc.RemoteCall;
import com.linda.framework.rpc.RpcObject;
import com.linda.framework.rpc.net.RpcSender;

public interface RpcFilterChain {
	
	public void nextFilter(RpcObject rpc,RemoteCall call,RpcSender sender);
	
	public void addRpcFilter(RpcFilter filter);
	
	public void startFilter(RpcObject rpc,RemoteCall call,RpcSender sender);
	
}
