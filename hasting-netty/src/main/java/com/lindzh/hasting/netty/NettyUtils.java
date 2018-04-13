package com.lindzh.hasting.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;
import io.netty.channel.socket.oio.OioSocketChannel;
import io.netty.handler.logging.LoggingHandler;

import com.lindzh.hasting.rpc.net.AbstractRpcAcceptor;
import com.lindzh.hasting.rpc.net.AbstractRpcConnector;
import com.lindzh.hasting.netty.codec.RpcNettyDecoder;
import com.lindzh.hasting.netty.codec.RpcNettyEncoder;
import com.lindzh.hasting.netty.handler.NettyRpcInBoundHandler;

public class NettyUtils {
	
	public static ServerBootstrap buildServerBootStrap(EventLoopGroup group,AbstractRpcAcceptor acceptor){
		final NettyRpcInBoundHandler inBoundHandler = new NettyRpcInBoundHandler();
		inBoundHandler.setParentAcceptor(acceptor);
		ServerBootstrap boot = new ServerBootstrap();
		if(group == null){
			group = new NioEventLoopGroup(20);
		}
		if(group instanceof NioEventLoopGroup){
			boot.channel(NioServerSocketChannel.class);
		}else if(group instanceof OioEventLoopGroup){
			boot.channel(OioServerSocketChannel.class);
		}
		boot.group(group);
		ChannelInitializer handler = new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();
				pipeline.addLast(new LoggingHandler());
				pipeline.addLast(new RpcNettyDecoder());
				pipeline.addLast(new RpcNettyEncoder());
				pipeline.addLast(inBoundHandler);
			}
		};
		boot.childHandler(handler);
		return boot;
	}
	
	public static Bootstrap buildBootStrap(EventLoopGroup group,AbstractRpcConnector connector){
		final NettyRpcInBoundHandler inBoundHandler = new NettyRpcInBoundHandler();
		inBoundHandler.setParentConnector(connector);
		Bootstrap boot = new Bootstrap();
		if(group == null){
			group = new NioEventLoopGroup(20);
		}
		if(group instanceof NioEventLoopGroup){
			boot.channel(NioSocketChannel.class);
		}else if(group instanceof OioEventLoopGroup){
			boot.channel(OioSocketChannel.class);
		}
		boot.group(group);
		ChannelInitializer handler = new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();
				pipeline.addLast(new LoggingHandler());
				pipeline.addLast(new RpcNettyDecoder());
				pipeline.addLast(new RpcNettyEncoder());
				pipeline.addLast(inBoundHandler);
			}
		};
		boot.handler(handler);
		return boot;
	}

}
