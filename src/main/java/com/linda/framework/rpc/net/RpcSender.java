package com.linda.framework.rpc.net;

import com.linda.framework.rpc.RpcObject;

public interface RpcSender {
	
	public boolean sendRpcObject(RpcObject rpc,int timeout);

}
