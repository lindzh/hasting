package com.linda.framework.rpc.nio;

import org.apache.log4j.Logger;

import com.linda.framework.rpc.RpcObject;
import com.linda.framework.rpc.net.AbstractRpcConnector;
import com.linda.framework.rpc.net.RpcCallListener;
import com.linda.framework.rpc.net.RpcSender;
import com.linda.framework.rpc.utils.RpcUtils.RpcType;

public class RpcNioTestClient implements RpcCallListener{
	
	public static Logger logger = Logger.getLogger(RpcNioTestClient.class);
	
	public static void main(String[] args) {
		RpcNioTestClient client = new RpcNioTestClient();
		String host = "127.0.0.1";
		int port = 4332;
		AbstractRpcConnector connector = new RpcNioConnector();
		connector.setHost(host);
		connector.setPort(port);
		connector.addRpcCallListener(client);
		connector.startService();
		new SendThread(connector).start();
	}

	@Override
	public void onRpcMessage(RpcObject rpc, RpcSender sender) {
		logger.info("client receive:"+rpc);
	}
	
	public static RpcObject createRpc(String str){
		RpcObject rpc = new RpcObject();
		rpc.setType(RpcType.INVOKE);
		rpc.setIndex(44);
		rpc.setThreadId(21);
		rpc.setData(str.getBytes());
		rpc.setLength(rpc.getData().length);
		return rpc;
	}

	public static class SendThread extends Thread{

		private AbstractRpcConnector connector;
		
		public SendThread(AbstractRpcConnector connector){
			this.connector = connector;
		}
		
		@Override
		public void run() {
			String prefix = "rpc test index ";
			int index = 1;
			while(true){
				RpcObject rpc = createRpc(prefix+index);
				logger.info("send:"+rpc);
				connector.sendRpcObject(rpc, 10000);
				index++;
				try {
					Thread.currentThread().sleep(3000L);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
}
