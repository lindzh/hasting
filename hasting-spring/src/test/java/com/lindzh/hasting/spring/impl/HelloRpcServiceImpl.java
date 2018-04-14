package com.lindzh.hasting.spring.impl;

import com.lindzh.hasting.spring.test.TestRemoteBean;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.lindzh.hasting.spring.annotation.RpcProviderService;
import com.lindzh.hasting.spring.test.HelloRpcService;
import com.lindzh.hasting.spring.test.TestBean;

@Service
@RpcProviderService(rpcServer="simpleRpcServer",version="1.0")
public class HelloRpcServiceImpl implements HelloRpcService{

	private Logger logger = Logger.getLogger(HelloRpcServiceImpl.class);
	
	@Override
	public void sayHello(String message,int tt) {
		logger.info("sayHello:"+message+" intValue:"+tt);
	}

	@Override
	public String getHello() {
		return "this is hello service";
	}

	@Override
	public TestRemoteBean getBean(TestBean bean, int id) {
		logger.info("id:"+id+" bean:"+bean.toString());
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
