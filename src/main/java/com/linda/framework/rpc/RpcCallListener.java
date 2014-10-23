package com.linda.framework.rpc;


public interface RpcCallListener {
	
	public void onRpcMessage(RpcObject rpc,RpcSend sender);

}
