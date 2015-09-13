package com.linda.framework.rpc;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class LoginRpcServiceImpl implements LoginRpcService{
	
	private Logger logger = Logger.getLogger(LoginRpcServiceImpl.class);
	
	private Map<String,String> cache = new HashMap<String,String>();

	@Override
	public boolean login(String username, String password) {
		//获取上下文附件
		String haha = (String)RpcContext.getContext().getAttachment("haha");
		System.out.println("login:user:"+username+" pass:"+password+" attach haha:"+haha);
		String pass = cache.get(username);
		//清除上下文附件
		RpcContext.getContext().clear();
		return pass!=null&&pass.equals(password);
	}
	
	public LoginRpcServiceImpl(){
		cache.put("linda", "123456");
		cache.put("test", "123456");
		cache.put("admin", "123456");
	}
}
