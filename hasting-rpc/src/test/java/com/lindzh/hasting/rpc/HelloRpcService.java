package com.lindzh.hasting.rpc;

public interface HelloRpcService {
	
	public void sayHello(String message,int tt);
	
	public String getHello();
	
	public TestRemoteBean getBean(TestBean bean,int id);
	
	public int callException(boolean exception);

}
