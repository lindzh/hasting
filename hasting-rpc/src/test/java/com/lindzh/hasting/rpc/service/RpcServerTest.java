package com.lindzh.hasting.rpc.service;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.lindzh.hasting.rpc.HelloRpcTestService;
import com.lindzh.hasting.rpc.LoginRpcServiceImpl;
import com.lindzh.hasting.rpc.RpcObject;
import com.lindzh.hasting.rpc.filter.RpcFilter;
import com.lindzh.hasting.rpc.filter.RpcFilterChain;
import com.lindzh.hasting.rpc.server.AbstractRpcServer;
import com.lindzh.hasting.rpc.server.ConcurrentRpcServer;
import org.apache.log4j.Logger;

import com.lindzh.hasting.rpc.HelloRpcService;
import com.lindzh.hasting.rpc.HelloRpcServiceImpl;
import com.lindzh.hasting.rpc.HelloRpcTestServiceImpl;
import com.lindzh.hasting.rpc.LoginRpcService;
import com.lindzh.hasting.rpc.RemoteCall;
import com.lindzh.hasting.rpc.monitor.StatMonitor;
import com.lindzh.hasting.rpc.net.RpcSender;
import com.lindzh.hasting.rpc.utils.RpcUtils;

public class RpcServerTest {
	
	private static Logger logger = 	Logger.getLogger(RpcServerTest.class);
	
	private static class ClientFilter implements RpcFilter {

		private HashSet<String> hosts = new HashSet<String>();
		
		@Override
		public void doFilter(RpcObject rpc, RemoteCall call, RpcSender sender,
                             RpcFilterChain chain) {
			String host = rpc.getHost()+":"+rpc.getPort();
			hosts.add(host);
			chain.nextFilter(rpc, call, sender);
		}
	}
	
	private static class StatThread extends Thread {
		
		public StatThread(StatMonitor monitor){
			this.monitor = monitor;
		}

		private StatMonitor monitor;
		
		@Override
		public void run() {
			while(true){
				Map<Long, Long> stat = monitor.getRpcStat();
				Set<Long> minutes = stat.keySet();
				for(long minute:minutes){
					long cc = stat.get(minute);
					long tps = cc/60;
					logger.info("time:"+new Date(minute)+" count:"+cc+" tps:"+tps);
				}
				try {
					Thread.currentThread().sleep(RpcUtils.MINUTE);
				} catch (InterruptedException e) {
					break;
				}
			}
		}
		
		
	}
	
	public static void main(String[] args) throws InterruptedException {
		
		long sleep = 300000;
		
		String host = "0.0.0.0";
		int port = 4332;
		
		int threadCount = 20;
		
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
				}
			}
		}
		
		AbstractRpcServer server = new ConcurrentRpcServer();
		//server.setAcceptor(new RpcOioAcceptor());
		server.setHost(host);
		server.setPort(port);
		
		server.setExecutorThreadCount(threadCount);
		
		HelloRpcService helloRpcServiceImpl = new HelloRpcServiceImpl();
		
		server.register(HelloRpcService.class, helloRpcServiceImpl);
		
		HelloRpcTestServiceImpl obj2 = new HelloRpcTestServiceImpl();
		
		server.register(HelloRpcTestService.class, obj2);
		
		LoginRpcService loginService = new LoginRpcServiceImpl();
		
		server.register(LoginRpcService.class, loginService);
		
		//server.addRpcFilter(new MyTestRpcFilter());
		
		//server.addRpcFilter(new RpcLoginCheckFilter());
		
		ClientFilter clientFilter = new ClientFilter();
		
		server.addRpcFilter(clientFilter);
		
		StatisticsFilter statisticsFilter = new StatisticsFilter();
		
		server.addRpcFilter(statisticsFilter);
		
		StatThread thread = new StatThread(server.getStatMonitor());
		
		thread.setDaemon(true);
		
		server.startService();
		
		thread.start();
		
		statisticsFilter.startService();
		
		logger.info("service started");
		
		Thread.currentThread().sleep(sleep);
		
		statisticsFilter.stopService();
		
		server.stopService();
		
		//logger.info("clients:"+clientFilter.hosts);
		
		logger.info("clientsSize:"+clientFilter.hosts.size()+" time:"+statisticsFilter.getTime()+" calls:"+statisticsFilter.getCall()+" tps:"+statisticsFilter.getTps());
		
		System.exit(0);
	}

}
