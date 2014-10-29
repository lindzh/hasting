package com.linda.framework.rpc.oio;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.apache.log4j.Logger;

import com.linda.framework.rpc.RpcObject;
import com.linda.framework.rpc.exception.RpcException;
import com.linda.framework.rpc.net.AbstractRpcConnector;
import com.linda.framework.rpc.utils.RpcUtils;

public class RpcOioConnector extends AbstractRpcConnector{
	
	private Socket socket;
	private DataInputStream dis;
	private DataOutputStream dos;
	private Logger logger = Logger.getLogger(RpcOioConnector.class);
	
	public RpcOioConnector(RpcOioWriter writer){
		super(writer);
		this.init();
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
			throw new RpcException(e);
		}
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
		this.getRpcWriter().stopService();
	}

	public DataOutputStream getOutputStream() {
		return dos;
	}
}
