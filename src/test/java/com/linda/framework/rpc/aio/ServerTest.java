package com.linda.framework.rpc.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ServerTest {
	private static Charset charset = Charset.forName("utf-8");
	private static CharsetEncoder encoder = charset.newEncoder();

	public static void main(String[] args) throws Exception {
		AsynchronousChannelGroup group = AsynchronousChannelGroup
				.withThreadPool(Executors.newFixedThreadPool(4));
		final AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel
				.open(group).bind(new InetSocketAddress("0.0.0.0", 8013));
		server.accept(null,
				new CompletionHandler<AsynchronousSocketChannel, Void>() {
					@Override
					public void completed(AsynchronousSocketChannel result,
							Void attachment) {
						server.accept(null, this); // 接受下一个连接
						try {
							String now = new Date().toString();
							ByteBuffer buffer = encoder.encode(CharBuffer
									.wrap(now + "\r\n"));
							// result.write(buffer, null, new
							// CompletionHandler<Integer,Void>(){...});
							// //callback or
							Future<Integer> f = result.write(buffer);
							f.get();
							System.out.println("sent to client: " + now);
							result.close();
						} catch (IOException | InterruptedException
								| ExecutionException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void failed(Throwable exc, Void attachment) {
						exc.printStackTrace();
					}
				});
		group.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
	}
}
