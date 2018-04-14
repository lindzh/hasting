package com.lindzh.hasting.spring.test;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

@Service
public class CallService {
	@Resource
	private HelloRpcService helloService;
	@Resource
	private HelloRpcTestService helloRpcTestService;
	@Resource
	private LoginRpcService loginRpcService;

	public void callHello(String msg,int tt){
		helloService.sayHello(msg, tt);
	}
	
	public void callLogin(String user,String pass){
		loginRpcService.login(user, pass);
	}
	
	public void callHelloTestIndex(int index,String key){
		helloRpcTestService.index(index, key);
	}

	public TestRemoteBean getBean(TestBean bean,int id){
		return helloService.getBean(bean, id);
	}
	
}
