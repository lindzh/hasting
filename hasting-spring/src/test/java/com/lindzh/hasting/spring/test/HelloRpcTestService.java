package com.lindzh.hasting.spring.test;

import com.lindzh.hasting.spring.annotation.RpcInvokerService;

@RpcInvokerService(rpcServer="simpleRpcClient")
public interface HelloRpcTestService {
	
	public String index(int index,String key);

}
