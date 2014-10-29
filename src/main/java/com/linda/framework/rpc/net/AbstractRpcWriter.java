package com.linda.framework.rpc.net;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import com.linda.framework.rpc.Service;

public abstract class AbstractRpcWriter implements Service,RpcOutputNofity{

	private Logger logger = Logger.getLogger(AbstractRpcWriter.class);
	private List<AbstractRpcConnector> connectors;
	private Thread sendThread;
	private int interval = 50;
	private boolean stop = false;
	private AtomicBoolean started = new AtomicBoolean(false);
	
	public AbstractRpcWriter(){
		connectors = Collections.synchronizedList(new LinkedList<AbstractRpcConnector>());
	}
	
	public void registerWrite(AbstractRpcConnector connector){
		connectors.add(connector);
	}
	
	public void unRegWrite(AbstractRpcConnector connector){
		connectors.remove(connector);
	}
	
	public void notifySend(AbstractRpcConnector connector){
		sendThread.interrupt();
	}
	
	@Override
	public void startService() {
		if(!started.get()){
			sendThread = new WriteThread();
			sendThread.start();
			started.set(true);
		}
	}

	@Override
	public void stopService() {
		stop = true;
		sendThread.interrupt();
	}
	
	public abstract boolean doSend(AbstractRpcConnector connector);
	
	private class WriteThread extends Thread{

		@Override
		public void run() {
			boolean hasSend = false;
			logger.info("nio common send service start");
			while(!stop){
				try {
					for(AbstractRpcConnector connector:connectors){
						hasSend |= doSend(connector);
					}
					if(!hasSend){
						Thread.currentThread().sleep(interval);
					}
					hasSend = false;
				} catch (InterruptedException e) {
					//notify to send
				}
			}
		}
	}
	
}
