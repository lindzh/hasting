package com.linda.framework.rpc.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Executors;

public class SimpleAioEchoService {

	public static void main(String[] args) throws IOException {
		
		AsynchronousChannelGroup channelGroup = AsynchronousChannelGroup.withCachedThreadPool(Executors.newFixedThreadPool(10), 10);
		
		final AsynchronousServerSocketChannel serverChannel = AsynchronousServerSocketChannel.open(channelGroup);
		
//		serverChannel.setOption(StandardSocketOptions.TCP_NODELAY, true);
		serverChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
		serverChannel.setOption(StandardSocketOptions.SO_RCVBUF, 16 * 1024);
		
		serverChannel.bind(new InetSocketAddress("127.0.0.1", 4321));
		
		Object attachment = null;
		
		final SocketReadHandler<Object> readHandler = new SocketReadHandler<Object>();
		final SocketWriteHandler<Object> writeHandler = new SocketWriteHandler<Object>();
		
		serverChannel.accept(attachment, new CompletionHandler<AsynchronousSocketChannel, Object>() {
			//新的连接建立
			@Override
			public void completed(AsynchronousSocketChannel socket,Object attachment) {
				System.out.println("new connection----------");
				try{
					SimpleAioConnector connector = new SimpleAioConnector<Object>(socket);
					connector.setAttachment(connector);
					connector.setReadHandler(readHandler);
					connector.setWriteHandler(writeHandler);
					connector.startService();
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					serverChannel.accept(attachment, this);
				}
			}

			//关闭连接
			@Override
			public void failed(Throwable e, Object attachment) {
				e.printStackTrace();
				serverChannel.accept(attachment, this);
			}
		});
		
		System.out.println("start listening----");
	}
}
