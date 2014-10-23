package com.linda.framework.rpc;

public interface RpcExceptionHandler {
	
	public void handleException(RpcObject rpc,RemoteCall call,Throwable e);

}
