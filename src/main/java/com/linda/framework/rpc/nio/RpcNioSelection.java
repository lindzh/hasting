package com.linda.framework.rpc.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import com.linda.framework.rpc.RpcObject;
import com.linda.framework.rpc.Service;
import com.linda.framework.rpc.exception.RpcException;
import com.linda.framework.rpc.exception.RpcNetExceptionHandler;
import com.linda.framework.rpc.net.AbstractRpcConnector;
import com.linda.framework.rpc.net.RpcOutputNofity;

public class RpcNioSelection implements Service,RpcOutputNofity,RpcNetExceptionHandler{
	
	private Selector selector;
	private boolean stop = false;
	private boolean started = false;
	private ConcurrentHashMap<SocketChannel,RpcNioConnector> connectorCache;
	private List<RpcNioConnector> connectors;
	private ConcurrentHashMap<ServerSocketChannel,RpcNioAcceptor> acceptorCache;
	private List<RpcNioAcceptor> acceptors;
	private AtomicBoolean inSelect = new AtomicBoolean(false);
	private static final int READ_OP = SelectionKey.OP_READ;
	private static final int READ_WRITE_OP = SelectionKey.OP_READ|SelectionKey.OP_WRITE;
	private static final int NONE_OP = 0;
	private LinkedList<Runnable> selectTasks = new LinkedList<Runnable>();
	
	private Logger logger = Logger.getLogger(RpcNioSelection.class);
	
	public RpcNioSelection(){
		try {
			selector = Selector.open();
			connectorCache = new ConcurrentHashMap<SocketChannel,RpcNioConnector>();
			connectors = new CopyOnWriteArrayList<RpcNioConnector>();
			acceptorCache = new ConcurrentHashMap<ServerSocketChannel,RpcNioAcceptor>();
			acceptors = new CopyOnWriteArrayList<RpcNioAcceptor>();
		} catch (IOException e) {
			throw new RpcException(e);
		}
	}
	
	public void register(RpcNioAcceptor acceptor){
		ServerSocketChannel channel = acceptor.getServerSocketChannel();
		try{
			channel.register(selector, SelectionKey.OP_ACCEPT);	
			acceptorCache.put(acceptor.getServerSocketChannel(), acceptor);
			acceptors.add(acceptor);
		}catch(Exception e){
			acceptor.handleNetException(e);
		}
	}
	
	public void unRegister(RpcNioAcceptor acceptor){
		ServerSocketChannel channel = acceptor.getServerSocketChannel();
		acceptorCache.remove(channel);
		acceptors.remove(acceptor);
	}
	
	public void register(RpcNioConnector connector){
		try{
			SelectionKey selectionKey = connector.getChannel().register(selector,READ_OP);
			this.initNewSocketChannel(connector.getChannel(),connector,selectionKey);
		}catch(Exception e){
			connector.handleNetException(e);
		}
	}
	
	public void unRegister(RpcNioConnector connector){
		connectorCache.remove(connector.getChannel());
		connectors.remove(connector);
	}
	
	private void initNewSocketChannel(SocketChannel channel,RpcNioConnector connector,SelectionKey selectionKey){
		if(connector.getAcceptor()!=null){
			connector.getAcceptor().addConnectorListeners(connector);
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
	
	private boolean doAccept(SelectionKey selectionKey){
		ServerSocketChannel server = (ServerSocketChannel)selectionKey.channel();
		RpcNioAcceptor acceptor = acceptorCache.get(server);
		try{
			SocketChannel client = server.accept();
			if(client!=null){
				client.configureBlocking(false);
				RpcNioConnector connector = new RpcNioConnector(client,this);
				connector.setAcceptor(acceptor);
				this.register(connector);
				connector.startService();
				return true;
			}
		}catch(Exception e){
			this.handSelectionKeyException(selectionKey, e);
		}
		return false;
	}
	
	private void fireRpc(RpcNioConnector connector,RpcObject rpc){
		rpc.setHost(connector.getRemoteHost());
		rpc.setPort(connector.getRemotePort());
		rpc.setRpcContext(connector.getRpcContext());
		connector.fireCall(rpc);
	}
	
	private boolean doRead(SelectionKey selectionKey){
		//logger.info("--------------read");
		boolean result = false;
		SocketChannel client = (SocketChannel)selectionKey.channel();
		RpcNioConnector connector = connectorCache.get(client);
		if(connector!=null){
			try{
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
						if(read<0){
							logger.info("read error--------------------------");
							this.handSelectionKeyException(selectionKey, new RpcException());
						}
						break;
					}
				}
			}catch(Exception e){
				this.handSelectionKeyException(selectionKey, e);
			}
		}
		return result;
	}
	
	private boolean doWrite(SelectionKey selectionKey) {
		boolean result = false;
		SocketChannel channel = (SocketChannel)selectionKey.channel();
		RpcNioConnector connector = connectorCache.get(channel);
		if(connector.isNeedToSend()){
			try{
				RpcNioBuffer connectorWriteBuf = connector.getRpcNioWriteBuffer();
				ByteBuffer channelWriteBuf = connector.getChannelWriteBuffer();
				while (connector.isNeedToSend()) {
					RpcObject rpc = connector.getToSend();
					connectorWriteBuf.writeRpcObject(rpc);
					channelWriteBuf.put(connectorWriteBuf.readBytes());
					channelWriteBuf.flip();
					int wantWrite = channelWriteBuf.limit()-channelWriteBuf.position();
					int write = 0;
					while (write < wantWrite) {
						write += channel.write(channelWriteBuf);
					}
					channelWriteBuf.clear();
					result = true;
				}
				if(!connector.isNeedToSend()){
					selectionKey.interestOps(READ_OP);
				}	
			}catch(Exception e){
				this.handSelectionKeyException(selectionKey, e);
			}
		}
		return result;
	}
	
	private void logState(){
		int acceptorLen = acceptors.size();
		int connectorLen = connectors.size();
		logger.info("acceptors:"+acceptorLen+" connectors:"+connectorLen);
	}
	
	private void handSelectionKeyException(final SelectionKey selectionKey,Exception e){
		SelectableChannel channel = selectionKey.channel();
		if(channel instanceof ServerSocketChannel){
			RpcNioAcceptor acceptor = acceptorCache.get(channel);
			if(acceptor!=null){
				logger.error("acceptor "+acceptor.getHost()+":"+acceptor.getPort()+" selection error "+e.getClass()+" "+e.getMessage()+" start to shutdown");
				acceptor.stopService();
			}
		}else{
			RpcNioConnector connector = connectorCache.get(channel);
			if(connector!=null){
				logger.error("connector "+connector.getHost()+":"+connector.getPort()+" selection error "+e.getClass()+" "+e.getMessage()+" start to shutdown");
				connector.stopService();
			}
		}
		this.logState();
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
			if (selectionKey.isWritable()) {
				result = doWrite(selectionKey);
			}
		}catch(Exception e){
			this.handSelectionKeyException(selectionKey, e);
		}
		return result;
	}

	private class SelectionThread extends Thread {
		@Override
		public void run() {
			logger.info("select thread has started");
			while (!stop) {
				if(RpcNioSelection.this.hasTask()){
					RpcNioSelection.this.runSelectTasks();
				}
				boolean needSend = checkSend();
				try {
					inSelect.set(true);
					if (needSend) {
						selector.selectNow();
					} else {
						selector.select();
					}
				} catch (IOException e) {
					RpcNioSelection.this.handleNetException(e);
				}
				inSelect.set(false);
				Set<SelectionKey> selectionKeys = selector.selectedKeys();
				for (SelectionKey selectionKey : selectionKeys) {
					doDispatchSelectionKey(selectionKey);
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
	
	private void addSelectTask(Runnable task){
		selectTasks.offer(task);
	}

	private boolean hasTask(){
		Runnable peek = selectTasks.peek();
		return peek!=null;
	}
	
	private void runSelectTasks(){
		Runnable peek = selectTasks.peek();
		while(peek!=null){
			peek = selectTasks.pop();
			peek.run();
			peek = selectTasks.peek();
		}
	}
	
	@Override
	public void handleNetException(Exception e) {
		logger.info("exception------------------------------->");
	}

}
