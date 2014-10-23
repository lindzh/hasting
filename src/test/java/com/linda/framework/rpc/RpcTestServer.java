package com.linda.framework.rpc;

import org.apache.log4j.Logger;

import com.linda.framework.rpc.RpcAcceptor;
import com.linda.framework.rpc.RpcServiceProvider;
import com.linda.framework.rpc.SimpleServerRemoteExecutor;

public class RpcTestServer {
	
	private static Logger logger = 	Logger.getLogger(RpcTestServer.class);
	
	public static void main(String[] args) throws InterruptedException {
		String host = "127.0.0.1";
		int port = 4332;
		
		RpcAcceptor acceptor = new RpcAcceptor();
		acceptor.setHost(host);
		acceptor.setPort(port);
		RpcServiceProvider provider = new RpcServiceProvider();
		
		SimpleServerRemoteExecutor proxy = new SimpleServerRemoteExecutor();
		
		Object obj = new HelloRpcServiceImpl();
		
		proxy.registerRemote(HelloRpcService.class, obj);
		
		HelloRpcTestServiceImpl obj2 = new HelloRpcTestServiceImpl();
		
		proxy.registerRemote(HelloRpcTestService.class, obj2);
		
		LoginRpcService loginService = new LoginRpcServiceImpl();
		
		proxy.registerRemote(LoginRpcService.class, loginService);
		
		provider.setExecutor(proxy);
		
		provider.getFilterChain().addRpcFilter(new MyTestRpcFilter());
		
		provider.getFilterChain().addRpcFilter(new RpcLoginCheckFilter());
		
		acceptor.addRpcCallListener(provider);
		
		acceptor.startService();
		
		logger.info("service started");
		
	}

}
