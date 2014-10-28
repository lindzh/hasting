package com.linda.framework.rpc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

import com.linda.framework.rpc.client.SimpleClientRemoteExecutor;
import com.linda.framework.rpc.client.SimpleClientRemoteProxy;
import com.linda.framework.rpc.net.AbstractRpcConnector;
import com.linda.framework.rpc.oio.RpcOioConnector;
import com.linda.framework.rpc.utils.RpcUtils;

public class RpcTestOioClient {
	
	private static Logger logger = 	Logger.getLogger(RpcTestOioClient.class);
	
	public static class CallThread extends Thread{
		
		private int execeptionCount = 0;
		
		private HelloRpcService helloRpcService;
		
		private HelloRpcTestService testService;
		
		private CountDownLatch latch;
		
		private int count;
		
		private long time;
		
		public CallThread(HelloRpcService helloRpcService,HelloRpcTestService testService,int count,CountDownLatch latch){
			this.helloRpcService = helloRpcService;
			this.testService = testService;
			this.count = count;
			this.latch = latch;
		}

		@Override
		public void run() {
			long start = RpcUtils.getNowInmilliseconds();
			for(int i=0;i<count;i++){
				TestBean bean = new TestBean();
				bean.setLimit(430);
				bean.setOffset(i);
				bean.setMessage("this is msg:"+i);
				bean.setOrder("asc");
				try{
					TestRemoteBean result = helloRpcService.getBean(bean, i+4311);
					logger.info("result:"+result.toString());
					//helloRpcService.callException(true);
					String index = testService.index(i, "index key "+i);
					logger.info("index:"+index);
				}catch(Exception e){
					e.printStackTrace();
					execeptionCount++;
				}
			}
			long stop = RpcUtils.getNowInmilliseconds();
			time = stop-start;
			logger.info("execute cost:"+time);
			latch.countDown();
		}

		public long getTime() {
			return time;
		}
	}
	
	public static void main(String[] args) {
		
		String host = "127.0.0.1";
		int port = 4332;
		AbstractRpcConnector connector = new RpcOioConnector();
		connector.setHost(host);
		connector.setPort(port);
		
		SimpleClientRemoteExecutor executor = new SimpleClientRemoteExecutor(connector);
		
		SimpleClientRemoteProxy proxy = new SimpleClientRemoteProxy();
		
		proxy.setRemoteExecutor(executor);
		
		proxy.startService();
		
		LoginRpcService loginService = proxy.registerRemote(LoginRpcService.class);
		
		HelloRpcService helloRpcService = proxy.registerRemote(HelloRpcService.class);
		
		HelloRpcTestService testService = proxy.registerRemote(HelloRpcTestService.class);
		
		logger.info("start client");
		
		helloRpcService.sayHello("this is HelloRpcService",564);
		
		loginService.login("linda", "123456");
		
		testService.index(43, "index client test");
		
		//loginService.login("linda", "123456");
		
		String hello = helloRpcService.getHello();
		
		int ex = helloRpcService.callException(false);
		
		logger.info("hello result:"+hello);
	
		logger.info("exResult:"+ex);
		
		int tcount = 3;
		int count = 100;
		List<CallThread> ths = new ArrayList<CallThread>(tcount);
		CountDownLatch latch = new CountDownLatch(tcount);
		for(int i=0;i<tcount;i++){
			CallThread thread = new CallThread(helloRpcService,testService,count,latch);
			thread.start();
			ths.add(thread);
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for(CallThread thread:ths){
			logger.info("thread:"+thread.getId()+" time:"+thread.getTime()+" execeptionCount:"+thread.execeptionCount);
		}
	}
	

}
