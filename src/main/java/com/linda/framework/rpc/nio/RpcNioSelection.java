package com.linda.framework.rpc.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.linda.framework.rpc.RpcObject;
import com.linda.framework.rpc.Service;
import com.linda.framework.rpc.exception.RpcException;
import com.linda.framework.rpc.net.RpcNetBase;
import com.linda.framework.rpc.utils.RpcUtils;

public class RpcNioSelection extends RpcNetBase implements Service{
	
	private Selector selector;
	private boolean stop = false;
	private boolean started = false;
	private ConcurrentHashMap<SocketChannel,RpcNioConnector> connectorCache;
	
	private Logger logger = Logger.getLogger(RpcNioSelection.class);
	
	public RpcNioSelection(){
		try {
			selector = Selector.open();
			connectorCache = new ConcurrentHashMap<SocketChannel,RpcNioConnector>();
		} catch (IOException e) {
			throw new RpcException(e);
		}
	}
	
	
	public void register(ServerSocketChannel channel,int ops) throws ClosedChannelException{
		channel.register(selector, ops);
	}

	public void register(SocketChannel channel,int ops) throws ClosedChannelException{
		channel.register(selector, ops);
		RpcNioConnector connector = this.genConnector(channel);
		this.initNewSocketChannel(channel,connector);
	}

	public void register(SocketChannel channel,int ops,Object att) throws ClosedChannelException{
		channel.register(selector, ops, att);
		RpcNioConnector connector = this.genConnector(channel);
		this.initNewSocketChannel(channel,connector);
	}
	
	public void register(RpcNioConnector connector,int ops) throws ClosedChannelException{
		connector.getChannel().register(selector, ops);
		this.initNewSocketChannel(connector.getChannel(),connector);
	}

	public void register(RpcNioConnector connector,int ops,Object att) throws ClosedChannelException{
		connector.getChannel().register(selector, ops, att);
		this.initNewSocketChannel(connector.getChannel(),connector);
	}
	
	private RpcNioConnector genConnector(SocketChannel channel){
		RpcNioConnector connector = new RpcNioConnector(channel,this);
		connector.startService();
		return connector;
	}
	
	private void initNewSocketChannel(SocketChannel channel,RpcNioConnector connector){
		connectorCache.put(channel, connector);
	}

	@Override
	public synchronized void startService() {
		if(!started){
			new SelectionThread().start();
			started = true;
		}
	}

	@Override
	public void stopService() {
		this.stop = true;
	}
	
	private void doAccept(SelectionKey selectionKey) throws IOException{
		ServerSocketChannel server = (ServerSocketChannel)selectionKey.channel();
		SocketChannel client = server.accept();
		if(client!=null){
			client.configureBlocking(false);
			this.register(client, SelectionKey.OP_READ|SelectionKey.OP_WRITE,ByteBuffer.allocate(1024*512));
		}
	}
	
	private void doRead(SelectionKey selectionKey) throws IOException{
		SocketChannel client = (SocketChannel)selectionKey.channel();
		RpcNioConnector connector = connectorCache.get(client);
		if(connector!=null){
			ByteBuffer buffer = (ByteBuffer)selectionKey.attachment();
			int read = client.read(buffer);
			if(read>0){
				RpcObject rpc = RpcUtils.readBuffer(buffer);
				rpc.setHost(connector.getRemoteHost());
				rpc.setPort(connector.getRemotePort());
				rpc.setRpcContext(connector.getRpcContext());
				this.fireCallListeners(rpc, connector);
				buffer.clear();
			}
		}
	}
	
	private void doWrite(SelectionKey selectionKey) throws IOException{
		SocketChannel client = (SocketChannel)selectionKey.channel();
		RpcNioConnector connector = connectorCache.get(client);
		while(connector.needSend()){
			ByteBuffer buffer = (ByteBuffer)selectionKey.attachment();
			RpcUtils.writeBuffer(buffer, connector.pop());
			buffer.flip();
			client.write(buffer);
			buffer.clear();
		}
	}
	
	private void doDispatchSelectionKey(SelectionKey selectionKey){
		try{
			if (selectionKey.isAcceptable()) {
				doAccept(selectionKey);
			}
			if (selectionKey.isWritable()) {
				doWrite(selectionKey);
			}
			if (selectionKey.isReadable()) {
				doRead(selectionKey);
			}
		}catch(IOException e){
			
		}
	}
	
	private class SelectionThread extends Thread {
		@Override
		public void run() {
			logger.info("select thread has started");
			while (!stop) {
				try {
					selector.select();
					Set<SelectionKey> selectionKeys = selector.selectedKeys();
					for (SelectionKey selectionKey : selectionKeys) {
						doDispatchSelectionKey(selectionKey);
					}
				} catch (IOException e) {
					throw new RpcException(e);
				}
			}
		}
	}

}
