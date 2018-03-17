package com.lindzh.hasting.rpc;

import com.lindzh.hasting.rpc.filter.RpcFilter;
import com.lindzh.hasting.rpc.filter.RpcFilterChain;
import com.lindzh.hasting.rpc.net.RpcSender;
import org.apache.log4j.Logger;

public class MyTestRpcFilter implements RpcFilter {
	private Logger logger = Logger.getLogger(MyTestRpcFilter.class);
	@Override
	public void doFilter(RpcObject rpc, RemoteCall call, RpcSender sender,
			RpcFilterChain chain) {
		logger.info("request ip:"+rpc.getHost()+" port:"+rpc.getPort());
		chain.nextFilter(rpc, call, sender);
	}
}
