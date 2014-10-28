package com.linda.framework.rpc.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.linda.framework.rpc.RpcObject;
import com.linda.framework.rpc.Service;
import com.linda.framework.rpc.exception.RpcException;
import com.linda.framework.rpc.net.RpcCallListener;
import com.linda.framework.rpc.net.RpcNetBase;
import com.linda.framework.rpc.utils.RpcUtils;

public class RpcNioSelection implements Service{
	
	private Selector selector;
	private boolean stop = false;
	private boolean started = false;
	private ConcurrentHashMap<SocketChannel,RpcNioConnector> connectorCache;
	private static int READ_WRITE_SELECTION_MODE = SelectionKey.OP_READ|SelectionKey.OP_WRITE;
	private RpcNioAcceptor acceptor;
	
	private Logger logger = Logger.getLogger(RpcNioSelection.class);
	
	public RpcNioSelection(RpcNioAcceptor acceptor){
		try {
			selector = Selector.open();
			connectorCache = new ConcurrentHashMap<SocketChannel,RpcNioConnector>();
			this.acceptor = acceptor;
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
	
	public void register(RpcNioConnector connector) throws ClosedChannelException{
		connector.getChannel().register(selector,READ_WRITE_SELECTION_MODE,ByteBuffer.allocate(RpcUtils.MEM_2M));
		this.initNewSocketChannel(connector.getChannel(),connector);
	}
	
	private RpcNioConnector genConnector(SocketChannel channel){
		RpcNioConnector connector = new RpcNioConnector(channel,this);
		connector.startService();
		return connector;
	}
	
	private void initNewSocketChannel(SocketChannel channel,RpcNioConnector connector){
		if(acceptor!=null){
			acceptor.addConnectorListeners(connector);
		}
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
	
	private boolean doAccept(SelectionKey selectionKey) throws IOException{
		ServerSocketChannel server = (ServerSocketChannel)selectionKey.channel();
		SocketChannel client = server.accept();
		if(client!=null){
			client.configureBlocking(false);
			this.register(client, SelectionKey.OP_READ|SelectionKey.OP_WRITE,ByteBuffer.allocate(RpcUtils.MEM_2M));
			return true;
		}
		return false;
	}
	
	private boolean doRead(SelectionKey selectionKey) throws IOException{
		boolean result = false;
		SocketChannel client = (SocketChannel)selectionKey.channel();
		RpcNioConnector connector = connectorCache.get(client);
		if(connector!=null){
			ByteBuffer buffer = (ByteBuffer)selectionKey.attachment();
			int read = client.read(buffer);
			if(read>0){
				buffer.flip();
				RpcObject rpc = RpcUtils.readBuffer(buffer);
				rpc.setHost(connector.getRemoteHost());
				rpc.setPort(connector.getRemotePort());
				rpc.setRpcContext(connector.getRpcContext());
				connector.fireCall(rpc);
				result = true;
			}
			buffer.clear();
		}
		return result;
	}
	
	private boolean doWrite(SelectionKey selectionKey) throws IOException{
		boolean result = false;
		SocketChannel client = (SocketChannel)selectionKey.channel();
		RpcNioConnector connector = connectorCache.get(client);
		ByteBuffer buffer = (ByteBuffer)selectionKey.attachment();
		while(connector.needSend()){
			RpcUtils.writeBuffer(buffer, connector.pop());
			buffer.flip();
			client.write(buffer);
			result=true;
		}
		buffer.clear();
		return result;
	}
	
	private boolean doDispatchSelectionKey(SelectionKey selectionKey){
		boolean result = false;
		try{
			if (selectionKey.isAcceptable()) {
				result = doAccept(selectionKey);
			}
			if (selectionKey.isWritable()) {
				result = doWrite(selectionKey);
			}
			if (selectionKey.isReadable()) {
				result = doRead(selectionKey);
			}
		}catch(IOException e){
			
		}
		return result;
	}
	
	private class SelectionThread extends Thread {
		@Override
		public void run() {
			logger.info("select thread has started");
			boolean hasTodo = false;
			while (!stop) {
				try {
					selector.select();
					Set<SelectionKey> selectionKeys = selector.selectedKeys();
					for (SelectionKey selectionKey : selectionKeys) {
						hasTodo |= doDispatchSelectionKey(selectionKey);
					}
					if(!hasTodo){
						Thread.currentThread().sleep(5L);
					}
				} catch (IOException e) {
					throw new RpcException(e);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
