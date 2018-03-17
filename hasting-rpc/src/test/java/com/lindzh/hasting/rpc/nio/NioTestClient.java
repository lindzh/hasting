package com.lindzh.hasting.rpc.nio;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.lindzh.hasting.rpc.RpcObject;
import com.lindzh.hasting.rpc.net.RpcCallListener;
import com.lindzh.hasting.rpc.net.RpcSender;
import org.apache.log4j.Logger;

import com.lindzh.hasting.rpc.net.AbstractRpcConnector;
import com.lindzh.hasting.rpc.utils.RpcUtils.RpcType;

public class NioTestClient implements RpcCallListener {
	
	public static Logger logger = Logger.getLogger(NioTestClient.class);
	private SimpleRpcNioSelector selection;
	private RpcNioConnector connector;
	private String host = "127.0.0.1";
	private int port = 4332;
	private int threadCount;
	private AtomicInteger send = new AtomicInteger(0);
	private AtomicInteger receive = new AtomicInteger(0);
	private List<Thread> threads;
	private AtomicBoolean started = new AtomicBoolean(false);
	private AtomicInteger cccc = new AtomicInteger(0);
	
	public NioTestClient(SimpleRpcNioSelector selection){
		this.selection = selection;
	}
	
	public static void main(String[] args) throws InterruptedException {
		SimpleRpcNioSelector selector = new SimpleRpcNioSelector();
		String ip = "127.0.0.1";
		int basePort = 3333;
		int clientCount = 5;
		int connectors = 2;
		int threadCount = 2;
		List<NioTestClient> clients = createClients(selector,ip,basePort,clientCount,connectors,threadCount);
		startService(clients);
		Thread.currentThread().sleep(60000);
		stopService(clients);
		Thread.currentThread().sleep(10000);
		printResult(clients);
	}
	
	public NioTestClient clone(){
		NioTestClient client = new NioTestClient(selection);
		client.host = host;
		client.port = port;
		client.threadCount = threadCount;
		return client;
	}
	
	public static void startService(List<NioTestClient> clients){
		int i = 0;
		for(NioTestClient client:clients){
			client.startService();
			i++;
		}
		logger.info("start client count:"+i);
	}
	
	public static void stopService(List<NioTestClient> clients){
		for(NioTestClient client:clients){
			client.stopService();
		}
	}
	
	public static void printResult(List<NioTestClient> clients){
		for(NioTestClient client:clients){
			client.printResult();
		}
	}
	
	public static List<NioTestClient> createClients(SimpleRpcNioSelector selection,String ip,int port,int clients,int connectors,int threadCount){
		List<NioTestClient> list = new LinkedList<NioTestClient>();
		int i=0;
		while(i<clients){
			NioTestClient client = new NioTestClient(selection);
			client.host = ip;
			client.port = port+i;
			client.threadCount = threadCount;
			
			list.add(client);
			int con = 0;
			while(con<connectors){
				list.add(client.clone());
				con++;
			}
			i++;
		}
		return list;
	}
	
	private List<Thread> startThread(AbstractRpcConnector connector,int count){
		LinkedList<Thread> list = new LinkedList<Thread>();
		int c = 0;
		Random random = new Random();
		while(c<count){
			int interval = random.nextInt(200);
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
		receive.incrementAndGet();
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

	public class SendThread extends Thread{

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
			logger.info("send thread:"+threadId+" start "+host+":"+port);
			while(true){
				RpcObject rpc = createRpc(prefix+index,threadId,index);
				connector.sendRpcObject(rpc, 10000);
				NioTestClient.this.send.incrementAndGet();
				index++;
				try {
					Thread.currentThread().sleep(interval);
				} catch (InterruptedException e) {
					break;
				}
			}
		}
	}

	public void printResult(){
		logger.info(this.host+":"+this.port+"  send:"+send.get()+" receive:"+receive.get());
	}
	
	public void startService() {
		if(!started.get()){
			started.set(true);
			connector = new RpcNioConnector(selection);
			connector.setHost(host);
			connector.setPort(port);
			connector.addRpcCallListener(this);
			connector.startService();
			threads = startThread(connector,threadCount);
			cccc.incrementAndGet();
			logger.info("start time:"+cccc.get());
		}

	}

	public void stopService() {
		for (Thread thread : threads) {
			thread.interrupt();
		}
		//connector.stopService();
	}
	
}
