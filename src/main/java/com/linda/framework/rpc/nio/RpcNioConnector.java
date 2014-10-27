package com.linda.framework.rpc.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.linda.framework.rpc.RpcObject;
import com.linda.framework.rpc.exception.RpcException;
import com.linda.framework.rpc.net.AbstractRpcConnector;
import com.linda.framework.rpc.net.RpcCallListener;

public class RpcNioConnector extends AbstractRpcConnector{
	
	private SocketChannel channel;
	private LinkedList<RpcObject> sendQueue = new LinkedList<RpcObject>();
	private RpcNioSelection selection;
	private Logger logger = Logger.getLogger(RpcNioConnector.class);

	public RpcNioConnector(SocketChannel socketChanel,RpcNioSelection selection){
		this.channel = socketChanel;
		this.selection = selection;
	}
	
	public RpcNioConnector(){
		selection = new RpcNioSelection();
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

	@Override
	public void stopService() {
		this.stop = true;
	}

	public boolean needSend(){
		RpcObject peek = sendQueue.peek();
		return peek!=null;
	}
	
	public RpcObject pop(){
		return sendQueue.pop();
	}
	
	@Override
	public boolean sendRpcObject(RpcObject rpc, int timeout) {
		int cost = 0;
		while(!sendQueue.offer(rpc)){
			cost +=3;
			try {
				Thread.currentThread().sleep(3);
			} catch (InterruptedException e) {
				throw new RpcException(e);
			}
			if(timeout>0&&cost>timeout){
				throw new RpcException("request time out");
			}
		}
		return true;
	}
	
	public void addRpcCallListener(RpcCallListener listener) {
		selection.addRpcCallListener(listener);
	}

	public SocketChannel getChannel() {
		return channel;
	}
}
