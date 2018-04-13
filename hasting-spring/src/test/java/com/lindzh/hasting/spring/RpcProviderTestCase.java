package com.lindzh.hasting.spring;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.lindzh.hasting.spring.test.CallService;
import org.junit.Test;

public class RpcProviderTestCase extends AbstractTestCase{

	@Resource
	private CallService callService;
	
	@Override
	public List<String> getLocations() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("classpath*:rpc-provider-config.xml");
		return list;
	}
	
	@Test
	public void startService(){
		callService.callHello("this is provider call", 564356);
		try {
			Thread.currentThread().sleep(1000000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
