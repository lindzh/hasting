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
	private ByteBuffer writeBuf;
	private ByteBuffer readBuf;
	private SelectionKey selectionKey;

	public RpcNioConnector(SocketChannel socketChanel,RpcNioSelection selection){
		this(selection);
		this.channel = socketChanel;
	}
	
	public RpcNioConnector(RpcNioSelection selection){
		super(null);
		if(selection==null){
			RpcNioWriter writer = new RpcNioWriter();
			this.selection = new RpcNioSelection(null,writer);
		}else{
			this.selection = selection;
		}
		this.setRpcWriter(this.selection.getWriter());
		this.initBuf();
	}
	
	private void initBuf(){
		writeBuf = ByteBuffer.allocate(RpcUtils.MEM_2M);
		readBuf = ByteBuffer.allocate(RpcUtils.MEM_2M);
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
			remotePort = remoteAddress.getPort();
			remoteHost = remoteAddress.getAddress().getHostAddress();
		}catch(IOException e){
			logger.info("connect to host "+host+" port "+port+" failed");
			throw new RpcException("connect to host error");
		}
	}
	
	public ByteBuffer getWriteBuf() {
		return writeBuf;
	}

	public ByteBuffer getReadBuf() {
		return readBuf;
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

}
