package com.linda.framework.rpc;

import org.apache.log4j.Logger;

public class SimpleRpcExceptionHandler implements RpcExceptionHandler{

	private Logger logger = Logger.getLogger(SimpleRpcExceptionHandler.class);
	
	@Override
	public void handleException(RpcObject rpc,RemoteCall call,Throwable e) {
		if(e instanceof RpcException){
			logger.info("rpcException "+e.getMessage());
		}else{
			e.printStackTrace();
		}
	}
}
