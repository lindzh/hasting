package com.linda.framework.rpc.aio;

import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import com.linda.framework.rpc.Service;

public class RpcAcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel,RpcAioAcceptor>,Service {

	private RpcReadCompletionHandler readHandler;
	private RpcWriteCompletionHandler writeHandler;
	
	@Override
	public void completed(AsynchronousSocketChannel channel, RpcAioAcceptor acceptor) {
		//
		RpcAioConnector connector = new RpcAioConnector(acceptor.getAioWriter(),channel);
		try{
			connector.setReadHandler(readHandler);
			connector.setWriteHandler(writeHandler);
			connector.startService();
		}catch(Exception e){
			connector.handleConnectorException(e);
		}finally{
			acceptor.getServerChannel().accept(acceptor, this);
		}
	}

	@Override
	public void failed(Throwable th, RpcAioAcceptor acceptor) {
		acceptor.handleFail(th, acceptor);
	}

	@Override
	public void startService() {
		readHandler = new RpcReadCompletionHandler();
		writeHandler = new RpcWriteCompletionHandler();
	}

	@Override
	public void stopService() {
		readHandler = null;
		writeHandler = null;
	}

}
