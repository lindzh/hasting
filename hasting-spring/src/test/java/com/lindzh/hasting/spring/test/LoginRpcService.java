package com.lindzh.hasting.spring.test;

import com.lindzh.hasting.spring.annotation.RpcInvokerService;

@RpcInvokerService(rpcServer="simpleRpcClient")
public interface LoginRpcService {
	
	public boolean login(String username,String password);

}
