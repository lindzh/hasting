package com.lindzh.hasting.rpc.exception;

/**
 * 网络异常处理
 * @author lindezhi
 * 2016年6月13日 下午4:48:03
 */
public interface RpcNetExceptionHandler {
	
	public void handleNetException(Exception e);

}
