package com.linda.framework.rpc;

import org.apache.log4j.Logger;

import com.linda.framework.rpc.RpcUtils.RpcType;

public class RpcServiceProvider implements RpcCallListener,RpcFilter,Service{
	
	private Logger logger = Logger.getLogger(RpcServiceProvider.class);
	
	private RemoteExecutor executor;
	
	private RpcSerializer serializer;
	
	private int timeout = 200;
	
	private RpcExceptionHandler exceptionHandler;
	
	private RpcFilterChain filterChain;
	
	public RpcServiceProvider(){
		serializer = new JdkSerializer();
		exceptionHandler = new SimpleRpcExceptionHandler();
		filterChain = new SimpleRpcFilterChain();
	}
	
	@Override
	public void onRpcMessage(RpcObject rpc, RpcSend sender) {
		RemoteCall call = this.deserializeCall(rpc, sender);
		try{
			if(call!=null){
				filterChain.startFilter(rpc, call, sender);
			}
		}catch(Exception e){
			this.handleException(rpc, call, sender, e);
		}
		
	}
	
	private RemoteCall deserializeCall(RpcObject rpc, RpcSend sender){
		try{
			return (RemoteCall)serializer.deserialize(rpc.getData());
		}catch(Exception e){
			this.handleException(rpc, null, sender, e);
			return null;
		}
	}
	
	private void execute(RemoteCall call,long threadId,int index,RpcSend sender){
		RpcObject rpc = this.createRpcObject(index);
		rpc.setThreadId(threadId);
		Object result = executor.invoke(call);
		rpc.setType(RpcType.SUC);
		if (result != null) {
			byte[] data = serializer.serialize(result);
			rpc.setLength(data.length);
			rpc.setData(data);
		}
		sender.sendRpcObject(rpc, timeout);
	}
	
	private RpcObject createRpcObject(int index){
		return new RpcObject(0,index, 0, null);
	}
	
	public RemoteExecutor getExecutor() {
		return executor;
	}

	public void setExecutor(RemoteExecutor executor) {
		this.executor = executor;
	}

	public RpcExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

	public void setExceptionHandler(RpcExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

	public RpcSerializer getSerializer() {
		return serializer;
	}

	public void setSerializer(RpcSerializer serializer) {
		this.serializer = serializer;
	}

	public RpcFilterChain getFilterChain() {
		return filterChain;
	}

	public void setFilterChain(RpcFilterChain filterChain) {
		this.filterChain = filterChain;
	}

	@Override
	public void startService() {
		filterChain.addRpcFilter(this);
	}

	@Override
	public void stopService() {
		
	}

	private void handleException(RpcObject rpc, RemoteCall call, RpcSend sender,Exception e){
		RpcUtils.handleException(exceptionHandler,rpc,call,e);
		if(rpc.getType()==RpcType.INVOKE){
			RpcObject respRpc = this.createRpcObject(rpc.getIndex());
			respRpc.setThreadId(rpc.getThreadId());
			respRpc.setType(RpcType.FAIL);
			String message = e.getMessage();
			if(message!=null){
				byte[] data = message.getBytes();
				respRpc.setLength(data.length);
				if(data.length>0){
					respRpc.setData(data);
				}
			}
			sender.sendRpcObject(respRpc, timeout);
		}
	}
	
	@Override
	public void doFilter(RpcObject rpc, RemoteCall call, RpcSend sender,
			RpcFilterChain chain) {
		int index = rpc.getIndex();
		if (rpc.getType() == RpcType.ONEWAY) {
			executor.oneway(call);
		} else if (rpc.getType() == RpcType.INVOKE) {
			this.execute(call, rpc.getThreadId(), index, sender);
		}
	}
}
