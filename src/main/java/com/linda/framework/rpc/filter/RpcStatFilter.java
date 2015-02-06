package com.linda.framework.rpc.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import com.linda.framework.rpc.RemoteCall;
import com.linda.framework.rpc.RpcObject;
import com.linda.framework.rpc.Service;
import com.linda.framework.rpc.monitor.StatMonitor;
import com.linda.framework.rpc.net.RpcSender;
import com.linda.framework.rpc.utils.RpcUtils;

/**
 * 访问频率监控，以分钟统计
 * @author linda
 *
 */
public class RpcStatFilter implements RpcFilter,Service,StatMonitor{
	
	private ConcurrentHashMap<Long, AtomicLong> staCache = new ConcurrentHashMap<Long, AtomicLong>();
	private AtomicBoolean running = new AtomicBoolean(false);
	private StatThread thread = new StatThread();

	@Override
	public void doFilter(RpcObject rpc, RemoteCall call, RpcSender sender,
			RpcFilterChain chain) {
		long now = RpcUtils.getNowMinute();
		AtomicLong cc = staCache.get(now);
		cc.incrementAndGet();
		chain.nextFilter(rpc, call, sender);
	}

	@Override
	public void startService() {
		List<Long> keys = getStatTime();
		ensureAtomicValue(keys);
		running.set(true);
		thread.start();
	}

	@Override
	public void stopService() {
		running.set(false);
		thread.interrupt();
	}
	
	private void ensureAtomicValue(List<Long> keys){
		Long max = null;
		if(keys.size()>0){
			max = Collections.max(keys);
		}
		if(max==null){
			max = RpcUtils.getNowMinute()-RpcUtils.MINUTE;
		}
		long key = max+RpcUtils.MINUTE;
		AtomicLong cc = staCache.get(key);
		if(cc==null){
			staCache.put(key, new AtomicLong(0));
		}
		key = max+RpcUtils.MINUTE*1;
		cc = staCache.get(key);
		if(cc==null){
			staCache.put(key, new AtomicLong(0));
		}
		key = max+RpcUtils.MINUTE*2;
		cc = staCache.get(key);
		if(cc==null){
			staCache.put(key, new AtomicLong(0));
		}
	}
	
	private void moveStatKeys(List<Long> keys,long now){
		for(Long k:keys){
			if(k<now-RpcUtils.MINUTE*30){
				staCache.remove(k);
			}
		}
	}
	
	private List<Long> getStatTime(){
		Set<Long> set = staCache.keySet();
		ArrayList<Long> keys = new ArrayList<Long>();
		keys.addAll(set);
		return keys;
	}
	
	@SuppressWarnings("unused")
	private class StatThread extends Thread{
		@Override
		public void run() {
			while(running.get()){
				long now = RpcUtils.getNowMinute();
				List<Long> keys = getStatTime();
				ensureAtomicValue(keys);
				moveStatKeys(keys,now);
				try {
					Thread.currentThread().sleep(RpcUtils.MINUTE);
				} catch (InterruptedException e) {
					break;
				}
			}
		}
	}

	@Override
	public Map<Long, Long> getRpcStat() {
		HashMap<Long, Long> result = new HashMap<Long, Long>();
		List<Long> times = this.getStatTime();
		for(Long time:times){
			AtomicLong ato = staCache.get(time);
			if(ato!=null){
				result.put(time,ato.get());
			}
		}
		return result;
	}
}
