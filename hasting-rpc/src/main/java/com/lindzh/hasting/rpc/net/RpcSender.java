package com.lindzh.hasting.rpc.net;

import com.lindzh.hasting.rpc.RpcObject;

public interface RpcSender {
	
	public boolean sendRpcObject(RpcObject rpc, int timeout);

}
