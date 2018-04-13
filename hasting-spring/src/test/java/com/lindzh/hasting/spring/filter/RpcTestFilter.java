package com.lindzh.hasting.spring.filter;

import com.lindzh.hasting.spring.annotation.RpcProviderFilter;
import org.apache.log4j.Logger;

import com.lindzh.hasting.rpc.RemoteCall;
import com.lindzh.hasting.rpc.RpcObject;
import com.lindzh.hasting.rpc.filter.RpcFilter;
import com.lindzh.hasting.rpc.filter.RpcFilterChain;
import com.lindzh.hasting.rpc.net.RpcSender;

@RpcProviderFilter(rpcServer="simpleRpcServer")
public class RpcTestFilter implements RpcFilter{
	
	private Logger logger = Logger.getLogger(RpcTestFilter.class);

	@Override
	public void doFilter(RpcObject rpc, RemoteCall call, RpcSender sender,
			RpcFilterChain chain) {
		logger.info(rpc.getHost()+":"+rpc.getPort()+" service:"+call.getService()+"."+call.getVersion());
		chain.nextFilter(rpc, call, sender);
	}
}
