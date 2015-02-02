package com.linda.framework.rpc.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import com.linda.framework.rpc.HelloRpcService;
import com.linda.framework.rpc.HelloRpcTestService;
import com.linda.framework.rpc.LoginRpcService;
import com.linda.framework.rpc.client.AbstractRpcClient;
import com.linda.framework.rpc.client.MultiRpcClient;
import com.linda.framework.rpc.exception.RpcException;
import com.linda.framework.rpc.monitor.RpcMonitorBean;
import com.linda.framework.rpc.monitor.RpcMonitorService;

public class RpcClientTest {
	
	private static Logger logger = 	Logger.getLogger(RpcClientTest.class);
	private String host = "127.0.0.1";
	private int port = 4332;
	private AbstractRpcClient client;
	private int clientCount = 5;
	private LoginRpcService loginService;
	private HelloRpcService helloRpcService;
	private HelloRpcTestService testService;
	private RpcMonitorService monitorService;
	private long sleep = 10;
	private long time = 10000L;
	private int threadCount = 5;
	private List<Thread> threads = new ArrayList<Thread>();
	private AtomicLong timeAll = new AtomicLong();
	private AtomicLong callAll = new AtomicLong();
	
	private boolean sleep(){
		if(sleep>0){
			try {
				Thread.currentThread().sleep(sleep);
			} catch (InterruptedException e) {
				return true;
			}
		}
		return false;
	}
	
	public void start(){
		client = new MultiRpcClient();
		//client.setConnector(new RpcOioConnector());
		client.setHost(host);
		client.setPort(port);
		((MultiRpcClient)client).setConnections(clientCount);
		client.startService();
		loginService = client.register(LoginRpcService.class);
		helloRpcService = client.register(HelloRpcService.class);
		testService = client.register(HelloRpcTestService.class);
		monitorService = client.register(RpcMonitorService.class);
		List<RpcMonitorBean> rpcServices = monitorService.getRpcServices();
		logger.info("rpcServices:"+rpcServices);
		startThreads();
	}
	
	private void startThreads(){
		int c = 0;
		while(c<threadCount){
			ExeThread thread = new ExeThread();
			thread.start();
			threads.add(thread);
			c++;
		}
	}
	
	public void shutdown(){
		for(Thread thread:threads){
			thread.interrupt();
		}
		client.stopService();
	}
	
	private class ExeThread extends Thread{
		Random random = new Random();
		@Override
		public void run() {
			boolean login = loginService.login("linda", "123456");
			logger.info("login result:"+login);
			long start = System.currentTimeMillis();
			long end = start+time;
			int call = 0;
			long begin = start;
			int fail = 0;
			while(start<end){
				try{
					int idx = random.nextInt(100000);
					helloRpcService.sayHello("this is HelloRpcService "+idx,idx);
					String index = testService.index(idx, "index client test "+idx);
					String hello = helloRpcService.getHello();
					int ex = helloRpcService.callException(false);
				}catch(RpcException e){
					fail++;
				}
				start = System.currentTimeMillis();
				call+=4;
				boolean inter = RpcClientTest.this.sleep();
				if(inter){
					break;
				}
				String ping = monitorService.ping();
				logger.info("ping resp:"+ping);
			}
			long endTime = System.currentTimeMillis();
			long cost = endTime-begin;
			logger.info("thread:"+Thread.currentThread().getId()+" call:"+call+" fail:"+fail+" cost:"+cost+" sleep:"+sleep);
			timeAll.addAndGet(cost);
			callAll.addAndGet(call);
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		String host = "127.0.0.1";
		int port = 4332;
		long sleep = 500;
		long time = 30000L;
		int threadCount = 3;
		int clients = 5;
		if(args!=null){
			for(String arg:args){
				if(arg.startsWith("-h")){
					host = arg.substring(2);
				}else if(arg.startsWith("-p")){
					port = Integer.parseInt(arg.substring(2));
				}else if(arg.startsWith("-s")){
					sleep = Long.parseLong(arg.substring(2));
				}else if(arg.startsWith("-th")){
					threadCount = Integer.parseInt(arg.substring(3));
				}else if(arg.startsWith("-t")){
					time = Long.parseLong(arg.substring(2));
				}else if(arg.startsWith("-c")){
					clients = Integer.parseInt(arg.substring(2));
				}
			}
		}
		logger.info("host:"+host+" port:"+port+" sleep:"+sleep+" thc:"+threadCount+" time:"+time+" connections:"+clients);
		int c = 0;
		long call = 0;
		long timeAll = 0;
		long myTime = time+3000+200*clients;
		List<RpcClientTest> tests = new ArrayList<RpcClientTest>();
		while(c<1){
			RpcClientTest test = new RpcClientTest();
			test.host = host;
			test.port = port;
			test.sleep = sleep;
			test.threadCount = threadCount;
			test.time = time;
			test.clientCount = clients;
			test.start();
			tests.add(test);
			Thread.currentThread().sleep(200);
			c++;
		}
		try {
			Thread.currentThread().sleep(myTime);
		} catch (InterruptedException e) {
		}
		for(RpcClientTest test:tests){
			call += test.callAll.get();
			timeAll += (test.timeAll.get()/1000);
			test.shutdown();
		}
		long exTime = timeAll/threadCount;
		double tps = call/exTime;
		double threadTps = call/timeAll;
		long myExeTime = time/1000;
		logger.info("callAll:"+call+" threadCount:"+threadCount+" timeAll:"+timeAll+" time:"+myExeTime+" tps:"+tps+" threadTps:"+threadTps+" connections:"+clients);
		System.exit(0);
	}

}
