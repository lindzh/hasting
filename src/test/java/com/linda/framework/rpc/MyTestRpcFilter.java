package com.linda.framework.rpc;

import org.apache.log4j.Logger;

import com.linda.framework.rpc.RemoteCall;
import com.linda.framework.rpc.RpcFilter;
import com.linda.framework.rpc.RpcFilterChain;
import com.linda.framework.rpc.RpcObject;
import com.linda.framework.rpc.RpcSend;

public class MyTestRpcFilter implements RpcFilter{

	private Logger logger = Logger.getLogger(MyTestRpcFilter.class);
	
	@Override
	public void doFilter(RpcObject rpc, RemoteCall call, RpcSend sender,
			RpcFilterChain chain) {
		logger.info("request ip:"+rpc.getHost()+" port:"+rpc.getPort());
		chain.nextFilter(rpc, call, sender);
	}

}
