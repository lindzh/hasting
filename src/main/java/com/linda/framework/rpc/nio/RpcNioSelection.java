package com.linda.framework.rpc.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.linda.framework.rpc.RpcObject;
import com.linda.framework.rpc.Service;
import com.linda.framework.rpc.exception.RpcException;
import com.linda.framework.rpc.net.AbstractRpcConnector;
import com.linda.framework.rpc.net.RpcOutputNofity;
import com.linda.framework.rpc.utils.NioUtils;

public class RpcNioSelection implements Service,RpcOutputNofity{
	
	private Selector selector;
	private boolean stop = false;
	private boolean started = false;
	private ConcurrentHashMap<SocketChannel,RpcNioConnector> connectorCache;
	private List<RpcNioConnector> connectors;
	private RpcNioAcceptor acceptor;
	
	
	private Logger logger = Logger.getLogger(RpcNioSelection.class);
	
	public RpcNioSelection(RpcNioAcceptor acceptor){
		this.acceptor = acceptor;
		try {
			selector = Selector.open();
			connectorCache = new ConcurrentHashMap<SocketChannel,RpcNioConnector>();
			connectors = new LinkedList<RpcNioConnector>();
		} catch (IOException e) {
			throw new RpcException(e);
		}
	}
	
	
	public void register(ServerSocketChannel channel,int ops) throws ClosedChannelException{
		channel.register(selector, ops);
	}
	
	public void register(RpcNioConnector connector) throws ClosedChannelException{
		SelectionKey selectionKey = connector.getChannel().register(selector,SelectionKey.OP_READ);
		this.initNewSocketChannel(connector.getChannel(),connector,selectionKey);
	}
	
	private void initNewSocketChannel(SocketChannel channel,RpcNioConnector connector,SelectionKey selectionKey){
		if(acceptor!=null){
			acceptor.addConnectorListeners(connector);
		}
		connector.setSelectionKey(selectionKey);
		connectorCache.put(channel, connector);
		connectors.add(connector);
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
			RpcNioConnector connector = new RpcNioConnector(client,this);
			this.register(connector);
			return true;
		}
		return false;
	}
	
	private boolean doRead(SelectionKey selectionKey) throws IOException{
		boolean result = false;
		SocketChannel client = (SocketChannel)selectionKey.channel();
		RpcNioConnector connector = connectorCache.get(client);
		if(connector!=null){
			synchronized(connector){
				ByteBuffer buffer = connector.getReadBuf();
				int read = client.read(buffer);
				if(read>0){
					buffer.flip();
					RpcObject rpc = NioUtils.readBuffer(buffer);
					rpc.setHost(connector.getRemoteHost());
					rpc.setPort(connector.getRemotePort());
					rpc.setRpcContext(connector.getRpcContext());
					connector.fireCall(rpc);
					result = true;
				}
				buffer.clear();
			}
		}
		return result;
	}
	
	public boolean doWrite(SelectionKey selectionKey) {
		boolean result = false;
		SocketChannel channel = (SocketChannel)selectionKey.channel();
		RpcNioConnector connector = connectorCache.get(channel);
		if(connector.isNeedToSend()){
			synchronized (connector) {
				while(connector.isNeedToSend()){
					ByteBuffer buffer = connector.getWriteBuf();
					RpcObject rpc = connector.getToSend();
					NioUtils.writeBuffer(buffer,rpc);
					buffer.flip();
					try {
						channel.write(buffer);
					} catch (IOException e) {
						e.printStackTrace();
					}
					buffer.clear();
					result=true;
				}
			}
			selectionKey.interestOps(SelectionKey.OP_READ);
		}
		return result;
	}
	
	private boolean doDispatchSelectionKey(SelectionKey selectionKey){
		boolean result = false;
		try{
			if (selectionKey.isAcceptable()) {
				result = doAccept(selectionKey);
			}
			if (selectionKey.isReadable()) {
				result = doRead(selectionKey);
			}
			if(selectionKey.isWritable()){
				result = doWrite(selectionKey);
			}
		}catch(IOException e){
			
		}
		return result;
	}

	private class SelectionThread extends Thread {
		@Override
		public void run() {
			logger.info("select thread has started");
			while (!stop) {
				checkSend();
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
	
	private void checkSend(){
		for(RpcNioConnector connector:connectors){
			if(connector.isNeedToSend()){
				SelectionKey selectionKey = connector.getChannel().keyFor(selector);
				selectionKey.interestOps(SelectionKey.OP_WRITE|SelectionKey.OP_READ);
			}
		}
	}

	@Override
	public void notifySend(AbstractRpcConnector connector) {
		selector.wakeup();
	}

}
