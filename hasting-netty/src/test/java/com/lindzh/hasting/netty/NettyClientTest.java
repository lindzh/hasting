package com.lindzh.hasting.netty;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

import com.lindzh.hasting.rpc.client.SimpleRpcClient;

public class NettyClientTest implements Runnable{
	
	private LoginRpcService loginRpcService;
	
	private CountDownLatch latch;
	
	private static Logger logger = Logger.getLogger(NettyClientTest.class);
	
	public NettyClientTest(LoginRpcService loginRpcService,CountDownLatch latch){
		this.loginRpcService = loginRpcService;
		this.latch = latch;
	}
	
	public static void main(String[] args) throws InterruptedException {
		SimpleRpcClient client = new SimpleRpcClient();
		client.setHost("127.0.0.1");
		client.setPort(5555);
		client.setConnectorClass(RpcNettyConnector.class);
		LoginRpcService loginRpcService = client.register(LoginRpcService.class);
		client.startService();
		int threadCount = 5;
		CountDownLatch latch = new CountDownLatch(threadCount);
		int i=0;
		while(i<threadCount){
			new Thread(new NettyClientTest(loginRpcService,latch)).start();
			i++;
		}
		latch.await();
		client.stopService();
	}

	@Override
	public void run() {
		int i = 0;
		Random random = new Random();
		int nn = random.nextInt(1000);
		while(i<1000){
			String user = "user-"+nn+"-"+i;
			String pass = "pass-"+nn+"-"+i;
			boolean login = loginRpcService.login("143243", "534543");
			logger.info("user-login user:"+user+" pass:"+pass+" result:"+login);
			i++;
			try {
				Thread.currentThread().sleep(random.nextInt(500));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		latch.countDown();
	}
}
