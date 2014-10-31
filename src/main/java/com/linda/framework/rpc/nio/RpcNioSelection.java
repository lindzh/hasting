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
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import com.linda.framework.rpc.RpcObject;
import com.linda.framework.rpc.Service;
import com.linda.framework.rpc.exception.RpcException;
import com.linda.framework.rpc.net.AbstractRpcConnector;
import com.linda.framework.rpc.net.RpcOutputNofity;

public class RpcNioSelection implements Service,RpcOutputNofity{
	
	private Selector selector;
	private boolean stop = false;
	private boolean started = false;
	private ConcurrentHashMap<SocketChannel,RpcNioConnector> connectorCache;
	private List<RpcNioConnector> connectors;
	private RpcNioAcceptor acceptor;
	private AtomicBoolean inSelect = new AtomicBoolean(false);
	private static final int READ_OP = SelectionKey.OP_READ;
	private static final int READ_WRITE_OP = SelectionKey.OP_READ|SelectionKey.OP_WRITE;
	
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
		SelectionKey selectionKey = connector.getChannel().register(selector,READ_OP);
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
			connector.startService();
			return true;
		}
		return false;
	}
	
	private void fireRpc(RpcNioConnector connector,RpcObject rpc){
		rpc.setHost(connector.getRemoteHost());
		rpc.setPort(connector.getRemotePort());
		rpc.setRpcContext(connector.getRpcContext());
		connector.fireCall(rpc);
	}
	
	private boolean doRead(SelectionKey selectionKey) throws IOException{
		boolean result = false;
		SocketChannel client = (SocketChannel)selectionKey.channel();
		RpcNioConnector connector = connectorCache.get(client);
		if(connector!=null){
			RpcNioBuffer connectorReadBuf = connector.getRpcNioReadBuffer();
			ByteBuffer channelReadBuf = connector.getChannelReadBuffer();
			while(!stop){
				int read = 0;
				while((read=client.read(channelReadBuf))>0){
					channelReadBuf.flip();
					byte[] readBytes = new byte[read];
					channelReadBuf.get(readBytes);
					connectorReadBuf.write(readBytes);
					channelReadBuf.clear();
					while(connectorReadBuf.hasRpcObject()){
						RpcObject rpc = connectorReadBuf.readRpcObject();
						this.fireRpc(connector, rpc);
					}
				}
				if(read<1){
					break;
				}
			}
		}
		return result;
	}
	
	private boolean doWrite(SelectionKey selectionKey) {
		boolean result = false;
		SocketChannel channel = (SocketChannel)selectionKey.channel();
		RpcNioConnector connector = connectorCache.get(channel);
		if(connector.isNeedToSend()){
			RpcNioBuffer connectorWriteBuf = connector.getRpcNioWriteBuffer();
			ByteBuffer channelWriteBuf = connector.getChannelWriteBuffer();
			while (connector.isNeedToSend()) {
				RpcObject rpc = connector.getToSend();
				connectorWriteBuf.writeRpcObject(rpc);
				channelWriteBuf.put(connectorWriteBuf.readBytes());
				channelWriteBuf.flip();
				int wantWrite = channelWriteBuf.limit()-channelWriteBuf.position();
				int write = 0;
				try {
					while(write<wantWrite){
						write += channel.write(channelWriteBuf);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				channelWriteBuf.clear();
				result = true;
			}
			if(!connector.isNeedToSend()){
				selectionKey.interestOps(READ_OP);
			}
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
			e.printStackTrace();
		}
		return result;
	}

	private class SelectionThread extends Thread {
		@Override
		public void run() {
			logger.info("select thread has started");
			while (!stop) {
				boolean needSend = checkSend();
				try {
					inSelect.set(true);
					if(needSend){
						selector.selectNow();
					}else{
						selector.select();	
					}
					inSelect.set(false);
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
	
	private boolean checkSend(){
		boolean needSend = false;
		for(RpcNioConnector connector:connectors){
			if(connector.isNeedToSend()){
				SelectionKey selectionKey = connector.getChannel().keyFor(selector);
				selectionKey.interestOps(READ_WRITE_OP);
				needSend = true;
			}
		}
		return needSend;
	}

	@Override
	public void notifySend(AbstractRpcConnector connector) {
		if(inSelect.get()){
			selector.wakeup();
		}
	}

}
