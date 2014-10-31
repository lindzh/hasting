package com.linda.framework.rpc.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import org.apache.log4j.Logger;

import com.linda.framework.rpc.exception.RpcException;
import com.linda.framework.rpc.net.AbstractRpcConnector;
import com.linda.framework.rpc.utils.RpcUtils;

public class RpcNioConnector extends AbstractRpcConnector{
	
	private SocketChannel channel;
	private RpcNioSelection selection;
	private Logger logger = Logger.getLogger(RpcNioConnector.class);
	private ByteBuffer channelWriteBuffer;
	private ByteBuffer channelReadBuffer;
	private SelectionKey selectionKey;
	
	private RpcNioBuffer rpcNioReadBuffer;
	private RpcNioBuffer rpcNioWriteBuffer;

	public RpcNioConnector(SocketChannel socketChanel,RpcNioSelection selection){
		this(selection);
		this.channel = socketChanel;
	}
	
	public RpcNioConnector(RpcNioSelection selection){
		super(null);
		if(selection==null){
			this.selection = new RpcNioSelection(null);
		}else{
			this.selection = selection;
		}
		this.initBuf();
	}
	
	private void initBuf(){
		channelWriteBuffer = ByteBuffer.allocate(RpcUtils.MEM_2M);
		channelReadBuffer = ByteBuffer.allocate(RpcUtils.MEM_2M);
		rpcNioReadBuffer = new RpcNioBuffer(RpcUtils.MEM_2M);
		rpcNioWriteBuffer = new RpcNioBuffer(RpcUtils.MEM_2M);
	}
	
	@Override
	public void startService() {
		try{
			if(channel==null){
				channel = SocketChannel.open();
				channel.connect(new InetSocketAddress(host,port));
				channel.configureBlocking(false);
				while(!channel.isConnected());
				logger.info("connect to host "+host+" port "+port+" success");
				selection.register(this);
				selection.startService();
			}
			InetSocketAddress remoteAddress = (InetSocketAddress)channel.getRemoteAddress();
			logger.info("remote address> "+remoteAddress.getAddress().getHostAddress()+":"+remoteAddress.getPort());
			remotePort = remoteAddress.getPort();
			remoteHost = remoteAddress.getAddress().getHostAddress();
		}catch(IOException e){
			logger.info("connect to host "+host+" port "+port+" failed");
			throw new RpcException("connect to host error");
		}
	}
	
	public ByteBuffer getChannelWriteBuffer() {
		return channelWriteBuffer;
	}

	public ByteBuffer getChannelReadBuffer() {
		return channelReadBuffer;
	}

	@Override
	public void stopService() {
		this.stop = true;
	}

	public SocketChannel getChannel() {
		return channel;
	}

	public SelectionKey getSelectionKey() {
		return selectionKey;
	}

	public void setSelectionKey(SelectionKey selectionKey) {
		this.selectionKey = selectionKey;
	}

	@Override
	public void notifySend() {
		selection.notifySend(this);
	}

	public RpcNioBuffer getRpcNioReadBuffer() {
		return rpcNioReadBuffer;
	}
	
	public RpcNioBuffer getRpcNioWriteBuffer() {
		return rpcNioWriteBuffer;
	}

}
