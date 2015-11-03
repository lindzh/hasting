package com.linda.framework.rpc.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.Executors;

import com.linda.framework.rpc.exception.RpcException;
import com.linda.framework.rpc.net.AbstractRpcAcceptor;

/**
 * 
 * @author lindezhi
 *
 */
public class RpcAioAcceptor extends AbstractRpcAcceptor{

	private AsynchronousServerSocketChannel serverChannel;
	
	private RpcAcceptCompletionHandler acceptHandler;
	
	private AsynchronousChannelGroup channelGroup;
	
	private RpcAioWriter aioWriter;
	
	private int channelGroupThreads = 20;
	
	public RpcAioAcceptor(){
		aioWriter = new RpcAioWriter();
	}
	
	public AsynchronousServerSocketChannel getServerChannel() {
		return serverChannel;
	}

	@Override
	public void startService() {
		super.startService();
		try {
			
			acceptHandler = new RpcAcceptCompletionHandler();
			acceptHandler.startService();
			channelGroup = AsynchronousChannelGroup.withThreadPool(Executors.newFixedThreadPool(channelGroupThreads));
			serverChannel = AsynchronousServerSocketChannel.open(channelGroup).bind(new InetSocketAddress(this.getHost(), this.getPort()));
			serverChannel.accept(this, acceptHandler);
			this.startListeners();
			this.fireStartNetListeners();
		} catch (IOException e) {
			throw new RpcException(e);
		}
	}

	@Override
	public void stopService() {
		super.stopService();
		this.closeChannel();
		stop = true;
		this.stopListeners();
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
		this.stopService();
		if(e instanceof RpcException){
			throw (RpcException)e;
		}else{
			throw new RpcException(e);
		}
	}

	public void handleFail(Throwable th, RpcAioAcceptor acceptor){
		acceptor.handleNetException(new RpcException(th));
	}

	public RpcAioWriter getAioWriter() {
		return aioWriter;
	}

	public void setAioWriter(RpcAioWriter aioWriter) {
		this.aioWriter = aioWriter;
	}

	public int getChannelGroupThreads() {
		return channelGroupThreads;
	}

	public void setChannelGroupThreads(int channelGroupThreads) {
		this.channelGroupThreads = channelGroupThreads;
	}
}
