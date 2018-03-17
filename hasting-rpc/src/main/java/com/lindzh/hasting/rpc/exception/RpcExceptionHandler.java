package com.lindzh.hasting.rpc.exception;

import com.lindzh.hasting.rpc.RemoteCall;
import com.lindzh.hasting.rpc.RpcObject;

/**
 * 异常处理
 * @author lindezhi
 * 2014年6月13日 下午4:47:49
 */
public interface RpcExceptionHandler {
	
	public void handleException(RpcObject rpc, RemoteCall call, Throwable e);

}
