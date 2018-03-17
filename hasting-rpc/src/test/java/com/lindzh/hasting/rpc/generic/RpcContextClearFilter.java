package com.lindzh.hasting.rpc.generic;

import com.lindzh.hasting.rpc.RemoteCall;
import com.lindzh.hasting.rpc.RpcContext;
import com.lindzh.hasting.rpc.RpcObject;
import com.lindzh.hasting.rpc.filter.RpcFilter;
import com.lindzh.hasting.rpc.filter.RpcFilterChain;
import com.lindzh.hasting.rpc.net.RpcSender;

public class RpcContextClearFilter implements RpcFilter {
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
