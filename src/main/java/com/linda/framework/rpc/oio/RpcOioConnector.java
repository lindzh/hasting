package com.linda.framework.rpc.oio;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.apache.log4j.Logger;

import com.linda.framework.rpc.RpcObject;
import com.linda.framework.rpc.exception.RpcException;
import com.linda.framework.rpc.exception.RpcNetExceptionHandler;
import com.linda.framework.rpc.net.AbstractRpcConnector;
import com.linda.framework.rpc.utils.RpcUtils;

public class RpcOioConnector extends AbstractRpcConnector implements RpcNetExceptionHandler{
	
	private Socket socket;
	private DataInputStream dis;
	private DataOutputStream dos;
	private Logger logger = Logger.getLogger(RpcOioConnector.class);
	
	public RpcOioConnector(RpcOioWriter writer){
		super(writer);
		this.init();
	}
	
	public RpcOioConnector(){
		this(null);
	}
	
	private void init(){
		if(this.getRpcWriter()==null){
			this.setRpcWriter(new RpcOioWriter());
		}
	}
	
	public RpcOioConnector(Socket socket,RpcOioWriter writer){
		this(writer);
		this.socket = socket;
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
			this.getRpcWriter().registerWrite(this);
			this.getRpcWriter().startService();
			new ClientThread().start();
		} catch (Exception e) {
			this.handleNetException(e);
		}
	}

	private class ClientThread extends Thread{
		@Override
		public void run() {
			while(!stop){
				RpcObject rpc = RpcUtils.readDataRpc(dis,RpcOioConnector.this);
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
		try {
			socket.close();
		} catch (IOException e) {
			//do nothing
		}
		rpcContext.clear();
		sendQueueCache.clear();
		executor.shutdown();
	}

	public DataOutputStream getOutputStream() {
		return dos;
	}

	@Override
	public void handleNetException(Exception e){
		this.getRpcWriter().unRegWrite(this);
		this.stopService();
		logger.error("connection caught io exception close");
		throw new RpcException(e);
	}
}
