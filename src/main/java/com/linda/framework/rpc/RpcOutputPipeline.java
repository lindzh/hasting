package com.linda.framework.rpc;

import java.io.DataOutputStream;
import java.util.LinkedList;

import org.apache.log4j.Logger;

public class RpcOutputPipeline implements Service{

	private LinkedList<RpcObject> rpcs = new LinkedList<RpcObject>();
	private DataOutputStream dos;
	private boolean stop = false;
	private Thread sendThread;
	
	private Logger logger = Logger.getLogger(RpcOutputPipeline.class);
	
	public RpcOutputPipeline(DataOutputStream dos){
		this.dos = dos;
	}
	
	@Override
	public void startService() {
		sendThread = new SendThread();
		sendThread.start();
	}

	@Override
	public void stopService() {
		stop = true;
		sendThread.interrupt();
	}
	
	public boolean addRpcObject(RpcObject rpc,int timeout){
		int cost = 0;
		while(!rpcs.offer(rpc)){
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
		return true;
	}
	
	private class SendThread extends Thread{
		
		private long lastExecuteTime = RpcUtils.getNowInmilliseconds();
		
		@Override
		public void run() {
			int index = 1;
			while(!stop){
				RpcObject rpc = rpcs.peek();
				if(rpc!=null){
					rpc = rpcs.pop();
//					logger.info("write index:"+index+" threadId:"+rpc.getThreadId()+",type:"+rpc.getType()+",index:"+rpc.getLength()+",len:"+rpc.getLength());
					index++;
					RpcUtils.writeDataRpc(rpc, dos);
					lastExecuteTime = RpcUtils.getNowInmilliseconds();
				}else{
					long now = RpcUtils.getNowInmilliseconds();
					if(now-lastExecuteTime>1000){
						try {
							Thread.currentThread().sleep(50);
						} catch (InterruptedException e) {
							break;
						}
					}
				}
			}
		}
	}
	
}
