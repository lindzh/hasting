package com.linda.framework.rpc;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class RpcAcceptor extends RpcNetBase implements Service{
	
	private String host;
	private int port;
	private ServerSocket server;
	private boolean stop = false;
	private List<RpcConnector> connectors;
	
	public RpcAcceptor(){
		super();
		connectors = new ArrayList<RpcConnector>();
	}
	
	public void startService(){
		try {
			server = new ServerSocket();
			server.bind(new InetSocketAddress(host,port));
			this.startListeners();
			new AcceptThread().start();
		} catch (Exception e) {
			throw new RpcException(e);
		}
	}
	
	private void startListeners(){
		for(RpcCallListener listener:callListeners){
			if(listener instanceof Service){
				Service service = (Service)listener;
				service.startService();
			}
		}
	}
	
	private void stopListeners(){
		for(RpcCallListener listener:callListeners){
			if(listener instanceof Service){
				Service service = (Service)listener;
				service.stopService();
			}
		}
	}

	@Override
	public void stopService() {
		stop = true;
		for(RpcConnector connector:connectors){
			connector.stopService();
		}
		this.stopListeners();
	}

	private class AcceptThread extends Thread{
		@Override
		public void run() {
			while(!stop){
				try {
					Socket socket = server.accept();
					RpcConnector connector = new RpcConnector(socket);
					for(RpcCallListener listener:callListeners){
						connector.addRpcCallListener(listener);
					}
					connector.startService();
				} catch (IOException e) {
					throw new RpcException(e);
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
	
}
