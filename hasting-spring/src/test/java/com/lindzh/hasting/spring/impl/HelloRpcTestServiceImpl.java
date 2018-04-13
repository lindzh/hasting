package com.lindzh.hasting.spring.impl;

import com.lindzh.hasting.spring.annotation.RpcProviderService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.lindzh.hasting.spring.test.HelloRpcTestService;

@Service
@RpcProviderService(rpcServer="simpleRpcServer")
public class HelloRpcTestServiceImpl implements HelloRpcTestService{

	private Logger logger = Logger.getLogger(HelloRpcTestServiceImpl.class);
	
	@Override
	public String index(int index, String key) {
		logger.info("index:"+index+" key:"+key);
		return "HelloRpcTestServiceImpl-"+index;
	}

}
