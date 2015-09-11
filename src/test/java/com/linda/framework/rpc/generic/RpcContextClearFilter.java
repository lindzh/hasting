package com.linda.framework.rpc.generic;

import com.linda.framework.rpc.RemoteCall;
import com.linda.framework.rpc.RpcContext;
import com.linda.framework.rpc.RpcObject;
import com.linda.framework.rpc.filter.RpcFilter;
import com.linda.framework.rpc.filter.RpcFilterChain;
import com.linda.framework.rpc.net.RpcSender;

public class RpcContextClearFilter implements RpcFilter{
	@Override
	public void doFilter(RpcObject rpc, RemoteCall call, RpcSender sender,
			RpcFilterChain chain) {
		try{
			chain.nextFilter(rpc, call, sender);
		}finally{
			System.out.println("clean rpc context");
			RpcContext.getContext().clear();
		}
	}
}
