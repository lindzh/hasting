package com.linda.framework.rpc.exception;

/**
 * rpc异常定义
 * @author lindezhi
 * 2014年6月13日 下午4:47:35
 */
public class RpcException extends RuntimeException{
	
	private static final long serialVersionUID = 6238589897120159526L;

	public RpcException(){
		super();
	}
	
	public RpcException(String message){
		super(message);
	}
	
	public RpcException(Throwable thr){
		super(thr);
	}

}
