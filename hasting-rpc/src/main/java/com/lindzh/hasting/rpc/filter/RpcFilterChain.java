package com.lindzh.hasting.rpc.filter;

import com.lindzh.hasting.rpc.RemoteCall;
import com.lindzh.hasting.rpc.RpcObject;
import com.lindzh.hasting.rpc.net.RpcSender;

/**
 * 过滤器责任链定义
 * @author lindezhi
 * 2016年6月13日 下午4:31:56
 */
public interface RpcFilterChain {
	
	/**
	 * 执行过滤
	 * @param rpc
	 * @param call
	 * @param sender
	 */
	public void nextFilter(RpcObject rpc,RemoteCall call,RpcSender sender);
	
	/**
	 * 按顺序添加过滤器
	 * @param filter
	 */
	public void addRpcFilter(RpcFilter filter);
	
	/**
	 * 启动chain
	 * @param rpc
	 * @param call
	 * @param sender
	 */
	public void startFilter(RpcObject rpc,RemoteCall call,RpcSender sender);
	
}
