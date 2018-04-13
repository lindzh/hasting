package com.lindzh.hasting.spring.test;

import com.lindzh.hasting.spring.annotation.RpcInvokerService;

@RpcInvokerService(rpcServer="simpleRpcClient",version = "1.0")
public interface HelloRpcService {
	
	public void sayHello(String message,int tt);
	
	public String getHello();
	
	public TestRemoteBean getBean(TestBean bean,int id);
	
	public int callException(boolean exception);

}
