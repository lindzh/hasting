package com.linda.framework.rpc;

import org.apache.log4j.Logger;

import com.linda.framework.rpc.filter.RpcFilter;
import com.linda.framework.rpc.filter.RpcFilterChain;
import com.linda.framework.rpc.net.RpcSender;

public class MyTestRpcFilter implements RpcFilter{

	private Logger logger = Logger.getLogger(MyTestRpcFilter.class);
	
	@Override
	public void doFilter(RpcObject rpc, RemoteCall call, RpcSender sender,
			RpcFilterChain chain) {
		logger.info("request ip:"+rpc.getHost()+" port:"+rpc.getPort());
		chain.nextFilter(rpc, call, sender);
	}

}
