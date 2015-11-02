package com.linda.framework.rpc.aio;

import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import com.linda.framework.rpc.Service;

public class RpcAcceptCompletionHandler<A> implements CompletionHandler<AsynchronousSocketChannel,A>,Service {

	private RpcAioAcceptor acceptor;
	private RpcReadCompletionHandler readHandler;
	private RpcWriteCompletionHandler writeHandler;
	
	
	public RpcAcceptCompletionHandler(RpcAioAcceptor acceptor){
		this.acceptor = acceptor;
	}
	
	@Override
	public void completed(AsynchronousSocketChannel channel, A att) {
		RpcAioConnector connector = new RpcAioConnector(channel);
		try{
			connector.setReadHandler(readHandler);
			connector.setWriteHandler(writeHandler);
			connector.startService();
		}catch(Exception e){
			//TODO
			connector.stopService();
		}finally{
			acceptor.getServerChannel().accept(att, this);
		}
	}

	@Override
	public void failed(Throwable th, A att) {
		acceptor.handleFail(th, att);
	}

	@Override
	public void startService() {
		readHandler = new RpcReadCompletionHandler();
		writeHandler = new RpcWriteCompletionHandler();
	}

	@Override
	public void stopService() {
		// TODO Auto-generated method stub
		
	}

}
