package com.lindzh.hasting.cluster;

import org.apache.log4j.Logger;

public class HelloRpcTestServiceImpl implements HelloRpcTestService{

	private Logger logger = Logger.getLogger(HelloRpcTestServiceImpl.class);
	
	@Override
	public String index(int index, String key) {
		//logger.info("index:"+index+" key:"+key);
		return "HelloRpcTestServiceImpl-"+index+" key:"+key;
	}

}
