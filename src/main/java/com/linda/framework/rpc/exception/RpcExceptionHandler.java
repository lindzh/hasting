package com.linda.framework.rpc.exception;

import com.linda.framework.rpc.RemoteCall;
import com.linda.framework.rpc.RpcObject;

/**
 * 异常处理
 * @author lindezhi
 * 2014年6月13日 下午4:47:49
 */
public interface RpcExceptionHandler {
	
	public void handleException(RpcObject rpc,RemoteCall call,Throwable e);

}
