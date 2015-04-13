package com.linda.framework.rpc.generic;

import com.linda.framework.rpc.HelloRpcService;
import com.linda.framework.rpc.HelloRpcServiceImpl;
import com.linda.framework.rpc.cluster.RpcClusterServer;
import com.linda.framework.rpc.net.RpcNetBase;
import com.linda.framework.rpc.oio.RpcOioAcceptor;

public class GenericServerTest {

	public static void main(String[] args) {
		RpcClusterServer server = new RpcClusterServer(){
			@Override
			public void onClose(RpcNetBase network, Exception e) {
				System.out.println("server close:"+network.getHost()+":"+network.getPort());
			}

			@Override
			public void onStart(RpcNetBase network) {
				System.out.println("server start:"+network.getHost()+":"+network.getPort());
			}

			@Override
			protected void doRegister(Class<?> clazz, Object ifaceImpl) {
				System.out.println("server reg:"+clazz+":"+ifaceImpl.getClass());
			}

			@Override
			protected void doRegister(Class<?> clazz, Object ifaceImpl,
					String version) {
				System.out.println("server reg:"+clazz+":"+ifaceImpl.getClass()+" v:"+version);
			}
		};
		server.setAcceptor(new RpcOioAcceptor());
		server.setHost("0.0.0.0");
		server.setPort(4445);
		server.register(HelloRpcService.class, new HelloRpcServiceImpl());
		server.startService();
		System.out.println("server startup");
	}
	
}
