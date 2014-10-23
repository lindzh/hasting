package com.linda.framework.rpc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

public class RpcConnector extends RpcNetBase implements Service,RpcSend{
	
	private String host = "127.0.0.1";
	private int port = 6521;
	
	private Socket socket;
	private DataInputStream dis;
	private DataOutputStream dos;
	private boolean stop = false;
	private RpcOutputPipeline pipeline;
	private ExecutorService executor = Executors.newFixedThreadPool(3);
	private Logger logger = Logger.getLogger(RpcConnector.class);
	private String remoteHost;
	private int remotePort;
	private ConcurrentHashMap<String,Object> rpcContext;
	
	public RpcConnector(){
		super();
		rpcContext = new ConcurrentHashMap<String,Object>();
	}
	
	public RpcConnector(Socket socket){
		super();
		this.socket = socket;
		rpcContext = new ConcurrentHashMap<String,Object>();
	}
	
	public void startService(){
		try {
			if(socket==null){
				socket = new Socket();
				socket.connect(new InetSocketAddress(host,port));
			}
			InetSocketAddress remoteAddress = (InetSocketAddress)socket.getRemoteSocketAddress();
			remotePort = remoteAddress.getPort();
			remoteHost = remoteAddress.getAddress().getHostAddress();
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());
			pipeline = new RpcOutputPipeline(dos);
			pipeline.startService();
			new ClientThread().start();
		} catch (Exception e) {
			throw new RpcException(e);
		}
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
	
	private void fireCall(final RpcObject rpc){
		executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					fireCallListeners(rpc, RpcConnector.this);
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("fire call err:" + e.getMessage());
				}
			}
		});
	}
	
	private class ClientThread extends Thread{
		@Override
		public void run() {
			while(!stop){
				RpcObject rpc = RpcUtils.readDataRpc(dis);
				if(rpc!=null){
					rpc.setHost(remoteHost);
					rpc.setPort(remotePort);
					rpc.setRpcContext(rpcContext);
					fireCall(rpc);
				}
			}
		}
	}

	@Override
	public void stopService() {
		stop = true;
		RpcUtils.close(dis, dos);
		rpcContext.clear();
		executor.shutdown();
		pipeline.stopService();
	}

	@Override
	public boolean sendRpcObject(RpcObject rpc, int timeout) {
		return pipeline.addRpcObject(rpc, timeout);
	}
}
