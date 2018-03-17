package com.lindzh.hasting.cluster;

import java.util.List;
import java.util.Set;

public interface HelloRpcService {
	
	public void sayHello(String message,int tt);
	
	public String getHello();
	
	public TestRemoteBean getBean(TestBean bean,int id);

	public List<String> getString(Set<String> hahah);

	public String[] hahahString(String[] haha);
	
	public int callException(boolean exception);

}
