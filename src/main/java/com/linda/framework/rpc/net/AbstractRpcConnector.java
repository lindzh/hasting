package com.linda.framework.rpc.net;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.linda.framework.rpc.RpcObject;
import com.linda.framework.rpc.Service;
import com.linda.framework.rpc.exception.RpcException;

public abstract class AbstractRpcConnector extends RpcNetBase implements Service,RpcSender{
	
	protected boolean stop = false;
	private Logger logger = Logger.getLogger(AbstractRpcConnector.class);
	protected String remoteHost;
	protected int remotePort;
	protected ConcurrentHashMap<String,Object> rpcContext;
	private RpcOutputNofity outputNotify;
	
	protected ConcurrentLinkedQueue<RpcObject> sendQueueCache = new ConcurrentLinkedQueue<RpcObject>();
	
	//写线程
	private AbstractRpcWriter rpcWriter;
	
	public AbstractRpcConnector(AbstractRpcWriter rpcWriter){
		super();
		this.rpcWriter = rpcWriter;
		rpcContext = new ConcurrentHashMap<String,Object>();
	}
	
	public ConcurrentHashMap<String, Object> getRpcContext() {
		return rpcContext;
	}

	public void setRpcContext(ConcurrentHashMap<String, Object> rpcContext) {
		this.rpcContext = rpcContext;
	}
	
	public String getRemoteHost() {
		return remoteHost;
	}

	public void setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
	}

	public int getRemotePort() {
		return remotePort;
	}

	public void setRemotePort(int remotePort) {
		this.remotePort = remotePort;
	}
	
	public boolean isNeedToSend(){
		RpcObject peek = sendQueueCache.peek();
		return peek!=null;
	}
	
	public RpcObject getToSend(){
		return sendQueueCache.poll();
	}
	
	@Override
	public boolean sendRpcObject(RpcObject rpc, int timeout) {
		int cost = 0;
		while(!sendQueueCache.offer(rpc)){
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
		this.notifySend();
		return true;
	}
	
	public void notifySend(){
		if(rpcWriter!=null){
			rpcWriter.notifySend(this);
		}
	}
	
	public AbstractRpcWriter getRpcWriter() {
		return rpcWriter;
	}

	public void setRpcWriter(AbstractRpcWriter rpcWriter) {
		this.rpcWriter = rpcWriter;
	}

	public RpcOutputNofity getOutputNotify() {
		return outputNotify;
	}

	public void setOutputNotify(RpcOutputNofity outputNotify) {
		this.outputNotify = outputNotify;
	}
	
	public boolean isStop() {
		return stop;
	}
	
	public abstract void handleConnectorException(Exception e);
	
	@Override
	public final void handleNetException(Exception e) {
		this.fireCloseNetListeners(e);
		this.handleConnectorException(e);
	}

	public void fireCall(final RpcObject rpc){
		this.getExecutorService().execute(new Runnable() {
			@Override
			public void run() {
				try {
					fireCallListeners(rpc, AbstractRpcConnector.this);
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("fire call err:" + e.getMessage());
				}
			}
		});
	}
}
