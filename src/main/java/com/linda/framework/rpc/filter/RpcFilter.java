package com.linda.framework.rpc.filter;

import com.linda.framework.rpc.RemoteCall;
import com.linda.framework.rpc.RpcObject;
import com.linda.framework.rpc.net.RpcSender;

public interface RpcFilter {
	
	public void doFilter(RpcObject rpc,RemoteCall call,RpcSender sender,RpcFilterChain chain);

}
