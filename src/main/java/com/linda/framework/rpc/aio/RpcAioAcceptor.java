package com.linda.framework.rpc.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;

import com.linda.framework.rpc.exception.RpcException;
import com.linda.framework.rpc.net.AbstractRpcAcceptor;

public class RpcAioAcceptor extends AbstractRpcAcceptor{

	private AsynchronousServerSocketChannel serverChannel;
	
	private RpcAcceptCompletionHandler<Object> acceptHandler;
	
	private AsynchronousChannelGroup channelGroup;
	
	public AsynchronousServerSocketChannel getServerChannel() {
		return serverChannel;
	}

	@Override
	public void startService() {
		super.startService();
		try {
			acceptHandler = new RpcAcceptCompletionHandler<Object>(this);
			acceptHandler.startService();
			channelGroup = AsynchronousChannelGroup.withThreadPool(this.getExecutorService());
			serverChannel = AsynchronousServerSocketChannel.open(channelGroup).bind(new InetSocketAddress(this.getHost(), this.getPort()));
			serverChannel.accept(null, acceptHandler);
		} catch (IOException e) {
			throw new RpcException(e);
		}
	}

	@Override
	public void stopService() {
		super.stopService();
		this.closeChannel();
	}
	
	private void closeChannel(){
		acceptHandler.stopService();
		try {
			this.serverChannel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.channelGroup.shutdown();
	}

	@Override
	public void handleNetException(Exception e) {
		
	}

	public void handleFail(Throwable th, Object att){
		
	}
}
