package com.linda.framework.rpc.nio;

import org.apache.log4j.Logger;

import com.linda.framework.rpc.RpcObject;
import com.linda.framework.rpc.net.AbstractRpcAcceptor;
import com.linda.framework.rpc.net.RpcCallListener;
import com.linda.framework.rpc.net.RpcSender;

public class NioTestServer implements RpcCallListener{
	
	private Logger logger = Logger.getLogger(NioTestServer.class);
	
	public static void main(String[] args) {
		NioTestServer server = new NioTestServer();
		
		String host = "127.0.0.1";
		int port = 4332;
		
		AbstractRpcAcceptor acceptor = new RpcNioAcceptor();
		acceptor.setHost(host);
		acceptor.setPort(port);
		acceptor.addRpcCallListener(server);
		acceptor.startService();
	}

	@Override
	public void onRpcMessage(RpcObject rpc, RpcSender sender) {
		logger.info("rpc server receive:"+rpc);
		sender.sendRpcObject(rpc, 1000);
	}

}
