package com.lindzh.hasting.netty;

import org.apache.log4j.Logger;

public class HelloRpcServiceImpl implements HelloRpcService{

	private Logger logger = Logger.getLogger(HelloRpcServiceImpl.class);
	
	@Override
	public void sayHello(String message,int tt) {
		//logger.info("sayHello:"+message+" intValue:"+tt);
	}

	@Override
	public String getHello() {
		return "this is hello service";
	}

	@Override
	public TestRemoteBean getBean(TestBean bean, int id) {
		//logger.info("id:"+id+" bean:"+bean.toString());
		TestRemoteBean remoteBean = new TestRemoteBean();
		remoteBean.setAction("fff-"+id);
		remoteBean.setAge(id*2);
		remoteBean.setName("serviceBean");
		return remoteBean;
	}

	@Override
	public int callException(boolean exception) {
		if(exception){
			throw new RuntimeException("happen");
		}
		return 1;
	}

}
