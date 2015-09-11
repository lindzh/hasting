package com.linda.framework.rpc;

import org.apache.log4j.Logger;

public class HelloRpcServiceImpl implements HelloRpcService{

	private Logger logger = Logger.getLogger(HelloRpcServiceImpl.class);
	
	@Override
	public void sayHello(String message,int tt) {
		Object attachment = RpcContext.getContext().getAttachment("myattachment");
		System.out.println("my attachment:"+attachment);
		System.out.println("sayHello:"+message+" intValue:"+tt);
	}

	@Override
	public String getHello() {
		return "this is hello service";
	}

	@Override
	public TestRemoteBean getBean(TestBean bean, int id) {
		Object attachment = RpcContext.getContext().getAttachment("myhaha");
		System.out.println("my attachment:"+attachment);
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
