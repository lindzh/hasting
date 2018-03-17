package com.lindzh.hasting.rpc.filter;

import com.lindzh.hasting.rpc.RemoteCall;
import com.lindzh.hasting.rpc.RpcObject;
import com.lindzh.hasting.rpc.net.RpcSender;

/**
 * rpc过滤器，类似于tomcat的servlet filter
 * @author lindezhi
 * 2016年6月13日 下午4:31:02
 */
public interface RpcFilter {
	
	/**
	 * 执行过滤
	 * @param rpc
	 * @param call
	 * @param sender
	 * @param chain
	 */
	public void doFilter(RpcObject rpc,RemoteCall call,RpcSender sender,RpcFilterChain chain);

}
