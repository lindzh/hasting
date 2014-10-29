package com.linda.framework.rpc.nio;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import com.linda.framework.rpc.RpcObject;
import com.linda.framework.rpc.net.AbstractRpcConnector;
import com.linda.framework.rpc.net.RpcCallListener;
import com.linda.framework.rpc.net.RpcSender;
import com.linda.framework.rpc.utils.RpcUtils.RpcType;

public class NioTestClient implements RpcCallListener{
	
	public static Logger logger = Logger.getLogger(NioTestClient.class);
	
	public static void main(String[] args) {
		NioTestClient client = new NioTestClient();
		String host = "127.0.0.1";
		int port = 4332;
		AbstractRpcConnector connector = new RpcNioConnector(null);
		connector.setHost(host);
		connector.setPort(port);
		connector.addRpcCallListener(client);
		connector.startService();
		List<Thread> list = startThread(connector,1);
		try {
			Thread.currentThread().sleep(100000L);
		} catch (InterruptedException e) {
		}
		for(Thread th:list){
			th.interrupt();
		}
		logger.info("stop------------------");
	}
	
	private static List<Thread> startThread(AbstractRpcConnector connector,int count){
		LinkedList<Thread> list = new LinkedList<Thread>();
		int c = 0;
		Random random = new Random();
		while(c<count){
			int interval = random.nextInt(1000);
			int index = random.nextInt(20000);
			SendThread thread = new SendThread(connector,interval,index);
			list.add(thread);
			thread.start();
			c++;
		}
		return list;
	}
	
	

	@Override
	public void onRpcMessage(RpcObject rpc, RpcSender sender) {
		logger.info("client receive:"+rpc);
	}
	
	public static RpcObject createRpc(String str,long id,int index){
		RpcObject rpc = new RpcObject();
		rpc.setType(RpcType.INVOKE);
		rpc.setIndex(index);
		rpc.setThreadId(id);
		rpc.setData(str.getBytes());
		rpc.setLength(rpc.getData().length);
		return rpc;
	}

	public static class SendThread extends Thread{

		private AbstractRpcConnector connector;
		private int interval;
		private int index;
		
		public SendThread(AbstractRpcConnector connector,int interval,int startIndex){
			this.connector = connector;
			this.interval = interval;
			this.index = startIndex;
		}
		
		@Override
		public void run() {
			String prefix = "rpc test index ";
			long threadId = Thread.currentThread().getId();
			logger.info("senf thread:"+threadId+" start");
			while(true){
				RpcObject rpc = createRpc(prefix+index,threadId,index);
				logger.info("send:"+rpc);
				connector.sendRpcObject(rpc, 10000);
				index++;
				try {
					Thread.currentThread().sleep(interval);
				} catch (InterruptedException e) {
					break;
				}
			}
		}
	}
	
}
