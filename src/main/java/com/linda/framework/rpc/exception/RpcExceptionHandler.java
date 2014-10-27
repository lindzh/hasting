package com.linda.framework.rpc.exception;

import com.linda.framework.rpc.RemoteCall;
import com.linda.framework.rpc.RpcObject;

public interface RpcExceptionHandler {
	
	public void handleException(RpcObject rpc,RemoteCall call,Throwable e);

}
