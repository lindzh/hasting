package com.linda.framework.rpc.net;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.linda.framework.rpc.RpcObject;
import com.linda.framework.rpc.Service;

public abstract class AbstractRpcConnector extends RpcNetBase implements Service,RpcSender{
	
	protected String host = "127.0.0.1";
	protected int port = 6521;
	protected boolean stop = false;
	protected ExecutorService executor = Executors.newFixedThreadPool(3);
	private Logger logger = Logger.getLogger(AbstractRpcConnector.class);
	protected String remoteHost;
	protected int remotePort;
	protected ConcurrentHashMap<String,Object> rpcContext;
	
	public AbstractRpcConnector(){
		super();
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
