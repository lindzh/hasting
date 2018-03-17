package com.lindzh.hasting.rpc.service;

import java.util.concurrent.atomic.AtomicLong;

import com.lindzh.hasting.rpc.RemoteCall;
import com.lindzh.hasting.rpc.RpcObject;
import com.lindzh.hasting.rpc.Service;
import com.lindzh.hasting.rpc.filter.RpcFilter;
import com.lindzh.hasting.rpc.filter.RpcFilterChain;
import com.lindzh.hasting.rpc.net.RpcSender;

public class StatisticsFilter implements RpcFilter,Service{
	
	private long start = 0;
	private long end = 0;
	private AtomicLong call = new AtomicLong(0);
	
	public void reset(){
		this.call.set(0);
		this.start = 0;
		this.end = 0;
	}

	@Override
	public void doFilter(RpcObject rpc, RemoteCall call, RpcSender sender,
                         RpcFilterChain chain) {
		this.call.incrementAndGet();
		chain.nextFilter(rpc, call, sender);
	}

	@Override
	public void startService() {
		this.start = System.currentTimeMillis();
	}

	@Override
	public void stopService() {
		this.end = System.currentTimeMillis();
	}
	
	public long getTime(){
		return this.end-this.start;
	}
	
	public long getCall(){
		return this.call.get();
	}
	
	public long getTps(){
		long time = this.getTime();
		long cc = this.getCall();
		if(time>0){
			return cc*1000/time;
		}else{
			return 0;
		}
	}
}
