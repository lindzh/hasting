package com.lindzh.hasting.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.AbstractChannel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;

import java.net.InetSocketAddress;

import org.apache.log4j.Logger;

import com.lindzh.hasting.rpc.RpcObject;
import com.lindzh.hasting.rpc.net.AbstractRpcConnector;
import com.lindzh.hasting.rpc.net.AbstractRpcWriter;

public class RpcNettyConnector extends AbstractRpcConnector{
	
	private AbstractChannel channel;
	
	private NioEventLoopGroup eventLoopGroup = null;
	
	private Logger logger = Logger.getLogger(RpcNettyConnector.class);
	
	public RpcNettyConnector(){
		this(null);
	}
	
	public RpcNettyConnector(AbstractChannel channel){
		super(null);
		this.channel = channel;
		if(this.channel!=null){
			this.setAddress(channel);
		}
	}
	
	private RpcNettyConnector(AbstractRpcWriter rpcWriter,String str) {
		super(rpcWriter);
	}
	
	private void setAddress(AbstractChannel channel){
		InetSocketAddress address = (InetSocketAddress)channel.remoteAddress();
		this.setHost(address.getHostName());
		this.setPort(address.getPort());
	}

	@Override
	public void startService() {
		super.startService();
		if(this.channel==null){
			eventLoopGroup = new NioEventLoopGroup(3);
			Bootstrap boot = NettyUtils.buildBootStrap(eventLoopGroup,this);
			boot.remoteAddress(this.getHost(), this.getPort());
			try {
				ChannelFuture f = boot.connect().sync();
				f.await();
				this.channel = (AbstractChannel)f.channel();
				this.fireStartNetListeners();
			} catch (InterruptedException e) {
				logger.info("interrupted start to exist");
				this.stopService();
			}
		}
	}

	@Override
	public void stopService() {
		super.stopService();
		this.rpcContext.clear();
		this.sendQueueCache.clear();
		this.callListeners.clear();
		channel.close();
		if(eventLoopGroup!=null){
			try {
				eventLoopGroup.shutdownGracefully().sync();
			} catch (InterruptedException e) {
			}
		}
	}

	@Override
	public boolean sendRpcObject(RpcObject rpc, int timeout) {
		ChannelFuture future = channel.writeAndFlush(rpc);
		try {
			future.await(timeout);
			return future.isSuccess();
		} catch (InterruptedException e) {
			return false;
		}
	}

	@Override
	public void handleConnectorException(Exception e) {
		logger.error(this.getHost()+":"+this.getPort()+" "+e+"     connector start to shutdown");
		this.stopService();
	}
}
