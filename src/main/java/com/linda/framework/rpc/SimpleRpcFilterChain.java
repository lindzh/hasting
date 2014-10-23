package com.linda.framework.rpc;

import java.util.ArrayList;
import java.util.List;

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
	public void nextFilter(RpcObject rpc, RemoteCall call, RpcSend sender) {
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
	public void startFilter(RpcObject rpc, RemoteCall call, RpcSend sender) {
		try{
			rpcFilterIndex.set(0);
			this.nextFilter(rpc, call, sender);
		}finally{
			rpcFilterIndex.remove();
		}
		
	}
}
