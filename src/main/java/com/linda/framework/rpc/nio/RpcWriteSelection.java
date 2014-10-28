package com.linda.framework.rpc.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.linda.framework.rpc.RpcObject;
import com.linda.framework.rpc.Service;
import com.linda.framework.rpc.exception.RpcException;
import com.linda.framework.rpc.utils.NioUtils;

public class RpcWriteSelection implements Service,RpcNotify{
	
	private Logger logger = Logger.getLogger(RpcWriteSelection.class);
	
	private ConcurrentHashMap<SocketChannel,RpcNioConnector> connectorCache;
	private Selector selector;
	private Thread sendThread;
	private int interval = 50;
	private boolean stop = false;
	
	public RpcWriteSelection(){
		connectorCache = new ConcurrentHashMap<SocketChannel,RpcNioConnector>();
		try {
			selector = Selector.open();
		} catch (IOException e) {
			throw new RpcException(e);
		}
	}
	
	public void registerWrite(RpcNioConnector connector){
		try{
			connector.getChannel().register(selector, SelectionKey.OP_WRITE);
			connectorCache.put(connector.getChannel(), connector);
		}catch(IOException e){
			throw new RpcException(e);
		}
	}
	
	public void sendNotify(SelectionKey key){
		sendThread.interrupt();
	}
	
	@Override
	public void startService() {
		sendThread = new WriteThread();
		sendThread.start();
	}

	@Override
	public void stopService() {
		stop = true;
		sendThread.interrupt();
	}
	
	public boolean doSend(SelectionKey key) throws IOException{
		boolean result = false;
		SocketChannel channel = (SocketChannel)key.channel();
		RpcNioConnector connector = connectorCache.get(channel);
		while(connector.needSend()){
			ByteBuffer buffer = connector.getWriteBuf();
			RpcObject rpc = connector.pop();
			NioUtils.writeBuffer(buffer,rpc);
			buffer.flip();
			channel.write(buffer);
			buffer.clear();
			result=true;
		}
		return result;
	}
	
	private class WriteThread extends Thread{

		@Override
		public void run() {
			boolean hasSend = false;
			logger.info("nio common send service start");
			while(!stop){
				try {
					selector.select();
					Set<SelectionKey> keys = selector.selectedKeys();
					for (SelectionKey key : keys) {
						hasSend |= doSend(key);
					}
					if(!hasSend){
						Thread.currentThread().sleep(interval);
					}
					hasSend = false;
				} catch (IOException e) {
					
				} catch (InterruptedException e) {
					//notify to send
				}
			}
		}
	}
	
	
}
