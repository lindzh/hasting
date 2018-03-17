package com.lindzh.hasting.rpc.net;

import com.lindzh.hasting.rpc.RpcObject;


public interface RpcCallListener {
	
	public void onRpcMessage(RpcObject rpc,RpcSender sender);

}
