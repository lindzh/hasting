package com.linda.framework.rpc;

import org.apache.log4j.Logger;

import com.linda.framework.rpc.net.AbstractRpcAcceptor;
import com.linda.framework.rpc.oio.RpcOioAcceptor;
import com.linda.framework.rpc.server.RpcServiceProvider;
import com.linda.framework.rpc.server.SimpleServerRemoteExecutor;

public class RpcTestOioServer {
	
	private static Logger logger = 	Logger.getLogger(RpcTestOioServer.class);
	
	public static void main(String[] args) throws InterruptedException {
		String host = "127.0.0.1";
		int port = 4332;
		
		AbstractRpcAcceptor acceptor = new RpcOioAcceptor();
		acceptor.setHost(host);
		acceptor.setPort(port);
		RpcServiceProvider provider = new RpcServiceProvider();
		
		SimpleServerRemoteExecutor proxy = new SimpleServerRemoteExecutor();
		
		HelloRpcServiceImpl obj = new HelloRpcServiceImpl();
		
		proxy.registerRemote(HelloRpcService.class, obj);
		
		HelloRpcTestServiceImpl obj2 = new HelloRpcTestServiceImpl();
		
		proxy.registerRemote(HelloRpcTestService.class, obj2);
		
		LoginRpcService loginService = new LoginRpcServiceImpl();
		
		proxy.registerRemote(LoginRpcService.class, loginService);
		
		provider.setExecutor(proxy);
		
		provider.addRpcFilter(new MyTestRpcFilter());
		
		provider.addRpcFilter(new RpcLoginCheckFilter());
		
		acceptor.addRpcCallListener(provider);
		
		acceptor.startService();
		
		logger.info("service started");
		
	}

}
