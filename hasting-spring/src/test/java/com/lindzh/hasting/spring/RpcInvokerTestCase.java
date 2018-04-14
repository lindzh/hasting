package com.lindzh.hasting.spring;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.lindzh.hasting.cluster.JSONUtils;
import com.lindzh.hasting.spring.test.TestBean;
import com.lindzh.hasting.spring.test.TestRemoteBean;
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
        TestBean testBean = new TestBean();
        testBean.setLimit(1600);
        testBean.setMessage("this is spring support test");
        testBean.setOffset(200);
        testBean.setOrder("order 9876");
        TestRemoteBean result = callService.getBean(testBean, 1000);
        System.out.println(JSONUtils.toJSON(result));
    }
	
}
