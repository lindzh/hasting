package com.linda.framework.rpc.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;

import org.apache.log4j.Logger;

import com.linda.framework.rpc.exception.RpcException;
import com.linda.framework.rpc.net.AbstractRpcAcceptor;

public class RpcNioAcceptor extends AbstractRpcAcceptor{
	
	private ServerSocketChannel serverSocketChannel;
	private RpcNioSelection selection;
	private Logger logger = Logger.getLogger(RpcNioAcceptor.class);
	
	public RpcNioAcceptor(RpcNioSelection selection){
		try {
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(false);
			this.selection = selection;
		} catch (IOException e) {
			this.handleNetException(e);
		}
	}
	
	public RpcNioAcceptor(){
		this(null);
	}
	
	public RpcNioSelection getSelection() {
		return selection;
	}

	public void setSelection(RpcNioSelection selection) {
		this.selection = selection;
	}

	@Override
	public void startService() {
		try {
			if(selection==null){
				selection = new RpcNioSelection();
			}
			serverSocketChannel.bind(new InetSocketAddress(host,port));
			selection.register(this);
			this.startListeners();
			selection.startService();
		} catch (IOException e) {
			this.handleNetException(e);
		}
	}

	@Override
	public void stopService() {
		if(serverSocketChannel!=null){
			try {
				serverSocketChannel.close();
				if(selection!=null){
					selection.stopService();
				}
			} catch (IOException e) {
				//do mothing
			}
		}
		this.stopListeners();
	}

	@Override
	public void handleNetException(Exception e) {
		logger.error("nio acceptor io exception,start to shut down service");
		this.stopService();
		throw new RpcException(e);
	}

	public ServerSocketChannel getServerSocketChannel() {
		return serverSocketChannel;
	}
}
