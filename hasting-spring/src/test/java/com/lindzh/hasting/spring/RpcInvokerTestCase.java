package com.lindzh.hasting.spring;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;

import com.lindzh.hasting.spring.test.CallService;

public class RpcInvokerTestCase extends AbstractTestCase{

	@Resource
	private CallService callService;
	
	@Override
	public List<String> getLocations() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("classpath*:rpc-invoker-config.xml");
		return list;
	}

	@Test
	public void test(){
		callService.callLogin("linda", "123456");
		callService.callHello("lindzgh", 50);
		callService.callHelloTestIndex(100, "543565-fwegfer");
	}
	
}
