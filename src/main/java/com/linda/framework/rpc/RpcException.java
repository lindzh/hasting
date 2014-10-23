package com.linda.framework.rpc;

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
