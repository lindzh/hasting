package com.linda.framework.rpc.oio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.linda.framework.rpc.exception.RpcException;
import com.linda.framework.rpc.net.AbstractRpcAcceptor;
import com.linda.framework.rpc.utils.SSLUtils;

public class RpcOioAcceptor extends AbstractRpcAcceptor{
	
	private ServerSocket server;
	private List<RpcOioConnector> connectors;
	private AbstractRpcOioWriter writer;
	private Logger logger = Logger.getLogger(RpcOioAcceptor.class);
	
	public RpcOioAcceptor(){
		this(null);
	}
	
	public RpcOioAcceptor(AbstractRpcOioWriter writer){
		super();
		if(writer==null){
			writer = new PooledRpcOioWriter();
		}else{
			this.writer = writer;
		}
		connectors = new ArrayList<RpcOioConnector>();
	}
	
	public void startService(){
		super.startService();
		try {
			server = SSLUtils.getServerSocketInstance(sslContext, sslMode);
			server.bind(new InetSocketAddress(host,port));
			this.startListeners();
			new AcceptThread().start();
		} catch (Exception e) {
			throw new RpcException(e);
		}
	}
	
	@Override
	public void stopService() {
		super.stopService();
		stop = true;
		for(RpcOioConnector connector:connectors){
			connector.stopService();
		}
		connectors.clear();
		this.stopListeners();
		try {
			server.close();
		} catch (IOException e) {
			//do nothing
		}
	}

	private class AcceptThread extends Thread{
		@Override
		public void run() {
			while(!stop){
				try {
					Socket socket = server.accept();
					RpcOioConnector connector = new RpcOioConnector(socket,writer);
					RpcOioAcceptor.this.addConnectorListeners(connector);
					connector.setExecutorService(RpcOioAcceptor.this.getExecutorService());
					connector.setExecutorSharable(true);
					connector.startService();
				} catch (IOException e) {
					RpcOioAcceptor.this.handleNetException(e);
				}
			}
		}
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public void handleNetException(Exception e) {
		logger.error("acceptor io exception,service start to shutdown");
		this.stopService();
	}
	
}
