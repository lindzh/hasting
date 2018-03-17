package com.lindzh.hasting.rpc.filter;

import java.util.ArrayList;
import java.util.List;

import com.lindzh.hasting.rpc.RemoteCall;
import com.lindzh.hasting.rpc.RpcObject;
import com.lindzh.hasting.rpc.exception.RpcException;
import com.lindzh.hasting.rpc.net.RpcSender;

/**
 * chain，使用thread local的方式
 * @author lindezhi
 * 2014年6月13日 下午4:40:20
 */
public class SimpleRpcFilterChain implements RpcFilterChain{
	
	private List<RpcFilter> filters = new ArrayList<RpcFilter>();
	
	private ThreadLocal<Integer> rpcFilterIndex = new ThreadLocal<Integer>();
	
	private int getAndIncrFilterIndex(){
		Integer index = rpcFilterIndex.get();
		if(index==null){
			index = 0;
		}
		rpcFilterIndex.set(index+1);
		return index;
	}

	@Override
	public void nextFilter(RpcObject rpc, RemoteCall call, RpcSender sender) {
		int index = getAndIncrFilterIndex();
		int size = filters.size();
		if(index>size-1){
			throw new RpcException("rpc filter call error");
		}
		RpcFilter filter = filters.get(index);
		filter.doFilter(rpc, call, sender, this);
	}
	
	public void addRpcFilter(RpcFilter filter){
		filters.add(filter);
	}

	@Override
	public void startFilter(RpcObject rpc, RemoteCall call, RpcSender sender) {
		try{
			rpcFilterIndex.set(0);
			this.nextFilter(rpc, call, sender);
		}finally{
			rpcFilterIndex.remove();
		}
		
	}
}
