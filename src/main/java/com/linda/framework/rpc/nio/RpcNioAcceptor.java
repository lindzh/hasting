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
	private AbstractRpcNioSelector selector;
	private Logger logger = Logger.getLogger(RpcNioAcceptor.class);
	
	public RpcNioAcceptor(AbstractRpcNioSelector selector){
		try {
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(false);
			this.selector = selector;
		} catch (IOException e) {
			this.handleNetException(e);
		}
	}
	
	public RpcNioAcceptor(){
		this(null);
	}
	
	public AbstractRpcNioSelector getSelector() {
		return selector;
	}

	public void setSelector(AbstractRpcNioSelector selector) {
		this.selector = selector;
	}

	@Override
	public void startService() {
		super.startService();
		try {
			if(selector==null){
				selector = new SimpleRpcNioSelector();
			}
			selector.startService();
			serverSocketChannel.socket().bind(new InetSocketAddress(host,port));
			//fix jdk 1.6 bind support
			//serverSocketChannel.bind(new InetSocketAddress(host,port));
			selector.register(this);
			this.startListeners();
			this.fireStartNetListeners();
		} catch (IOException e) {
			this.handleNetException(e);
		}
	}

	@Override
	public void stopService() {
		super.stopService();
		if(serverSocketChannel!=null){
			try {
				serverSocketChannel.close();
				if(selector!=null){
					selector.stopService();
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
