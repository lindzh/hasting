package com.linda.framework.rpc.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;

import com.linda.framework.rpc.exception.RpcException;
import com.linda.framework.rpc.net.AbstractRpcAcceptor;

public class RpcNioAcceptor extends AbstractRpcAcceptor{
	
	private ServerSocketChannel serverSocketChannel;
	private RpcNioSelection selection;
	
	public RpcNioAcceptor(){
		try {
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(false);
			selection = new RpcNioSelection(this);
		} catch (IOException e) {
			throw new RpcException(e);
		}
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
			serverSocketChannel.bind(new InetSocketAddress(host,port));
			selection.register(serverSocketChannel, SelectionKey.OP_ACCEPT);
			this.startListeners();
			selection.startService();
		} catch (IOException e) {
			throw new RpcException(e);
		}
	}

	@Override
	public void stopService() {
		selection.stopService();
		this.stopListeners();
	}
	
}
