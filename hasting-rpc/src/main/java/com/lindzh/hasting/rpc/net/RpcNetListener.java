package com.lindzh.hasting.rpc.net;

/**
 * 网络异常监听器，用于通知集群更新状态
 * @author Administrator
 *
 */
public interface RpcNetListener {

	public void onClose(RpcNetBase network,Exception e);

	public void onStart(RpcNetBase network);
	
}
