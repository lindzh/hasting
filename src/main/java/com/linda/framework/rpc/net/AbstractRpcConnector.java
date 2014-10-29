package com.linda.framework.rpc.net;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.linda.framework.rpc.RpcObject;
import com.linda.framework.rpc.Service;
import com.linda.framework.rpc.exception.RpcException;

public abstract class AbstractRpcConnector extends RpcNetBase implements Service,RpcSender{
	
	protected String host = "127.0.0.1";
	protected int port = 6521;
	protected boolean stop = false;
	protected ExecutorService executor = Executors.newFixedThreadPool(3);
	private Logger logger = Logger.getLogger(AbstractRpcConnector.class);
	protected String remoteHost;
	protected int remotePort;
	protected ConcurrentHashMap<String,Object> rpcContext;
	private RpcOutputNofity outputNotify;
	
	private LinkedList<RpcObject> sendQueueCache = new LinkedList<RpcObject>();
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

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
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
		return sendQueueCache.pop();
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

	public void fireCall(final RpcObject rpc){
		executor.execute(new Runnable() {
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
