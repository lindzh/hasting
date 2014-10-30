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
import com.linda.framework.rpc.utils.NioUtils;

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
	
	private boolean doRead(SelectionKey selectionKey) throws IOException{
		boolean result = false;
		SocketChannel client = (SocketChannel)selectionKey.channel();
		RpcNioConnector connector = connectorCache.get(client);
		if(connector!=null){
			ByteBuffer buffer = connector.getReadBuf();
			while(!stop){
				int read = client.read(buffer);
				if (read > 0) {
					buffer.flip();
					RpcObject rpc = NioUtils.readBuffer(buffer);
					rpc.setHost(connector.getRemoteHost());
					rpc.setPort(connector.getRemotePort());
					rpc.setRpcContext(connector.getRpcContext());
					connector.fireCall(rpc);
					result = true;
				}else{
					buffer.clear();
					break;
				}
			}
		}
		return result;
	}
	
	private boolean doRead0(SelectionKey selectionKey) throws IOException{
		boolean result = false;
		SocketChannel client = (SocketChannel)selectionKey.channel();
		RpcNioConnector connector = connectorCache.get(client);
		if(connector!=null){
			ByteBuffer buffer = connector.getReadBuf();
			int readTime = 0;
			while(!stop){
				int read = 0;
				while((read = client.read(buffer))>0){
					int limit = buffer.limit();
					int position = buffer.position();
					if (position >= NioUtils.RPC_PROTOCOL_HEAD_LEN) {
						buffer.flip();
						RpcObject rpc = NioUtils.readBuffer(buffer);
						if(position<rpc.getLength()+NioUtils.RPC_PROTOCOL_HEAD_LEN){
							//没有达到数据长度重新读取
							logger.info("----------------------->");
							buffer.position(position);
							buffer.limit(limit);
						}else{
							rpc.setHost(connector.getRemoteHost());
							rpc.setPort(connector.getRemotePort());
							rpc.setRpcContext(connector.getRpcContext());
							connector.fireCall(rpc);
							if(position>rpc.getLength()+NioUtils.RPC_PROTOCOL_HEAD_LEN){
								buffer.compact();
							}else{
								buffer.clear();
							}
							result = true;
							readTime++;
							if(readTime>=3){
								logger.info("read too much start to read next time---------->read");
								read = 0;
								break;
							}
						}
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
			while (connector.isNeedToSend()) {
				ByteBuffer buffer = connector.getWriteBuf();
				RpcObject rpc = connector.getToSend();
				NioUtils.writeBuffer(buffer, rpc);
				buffer.flip();
				try {
					channel.write(buffer);
				} catch (IOException e) {
					e.printStackTrace();
				}
				buffer.clear();
				result = true;
			}
			selectionKey.interestOps(READ_OP);
		}
		return result;
	}
	
	private boolean doWrite0(SelectionKey selectionKey) {
		boolean result = false;
		SocketChannel channel = (SocketChannel)selectionKey.channel();
		RpcNioConnector connector = connectorCache.get(channel);
		if(connector.isNeedToSend()){
			int writeTime = 0;
			while (connector.isNeedToSend()) {
				ByteBuffer buffer = connector.getWriteBuf();
				RpcObject rpc = connector.getToSend();
				NioUtils.writeBuffer(buffer, rpc);
				buffer.flip();
				int wantWrite = buffer.limit()-buffer.position();
				int write = 0;
				try {
					while(write<wantWrite){
						write += channel.write(buffer);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				buffer.clear();
				result = true;
				writeTime++;
				if(writeTime>=3){
					logger.info("write too much start-------------->write");
					break;
				}
			}
			selectionKey.interestOps(READ_OP);
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
				result = doRead0(selectionKey);
			}
			if(selectionKey.isWritable()){
				result = doWrite0(selectionKey);
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
				checkSend();
				try {
					inSelect.set(true);
					selector.select();
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
	
	private void checkSend(){
		for(RpcNioConnector connector:connectors){
			if(connector.isNeedToSend()){
				SelectionKey selectionKey = connector.getChannel().keyFor(selector);
				selectionKey.interestOps(READ_WRITE_OP);
			}
		}
	}

	@Override
	public void notifySend(AbstractRpcConnector connector) {
		if(inSelect.get()){
			selector.wakeup();
		}
	}

}
