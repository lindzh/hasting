package com.linda.framework.rpc;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.linda.framework.rpc.RpcUtils.RpcType;

public class SimpleClientRemoteExecutor implements RemoteExecutor,RpcCallListener,Service{
	
	private int timeout = 10000;
	private RpcConnector connector;
	private AtomicInteger index = new AtomicInteger(10000);
	private RpcSync clientRpcSync;
	private RpcSerializer serializer;
	
	private Logger logger = Logger.getLogger(SimpleClientRemoteExecutor.class);
	
	public SimpleClientRemoteExecutor(RpcConnector connector){
		connector.addRpcCallListener(this);
		this.connector = connector;
		clientRpcSync = new SimpleFutureRpcSync();
		serializer = new JdkSerializer();
	}
	
	private ConcurrentHashMap<String,RpcCallSync> rpcCache = new ConcurrentHashMap<String,RpcCallSync>(); 
	
	@Override
	public void oneway(RemoteCall call) {
		byte[] buffer = serializer.serialize(call);
		int length = buffer.length;
		RpcObject request = new RpcObject(ONEWAY,this.genIndex(),length,buffer);
		connector.sendRpcObject(request, timeout);
	}
	
	private String genRpcCallCacheKey(long threadId,int index){
		return "rpc_"+threadId+"_"+index;
	}

	@Override
	public Object invoke(RemoteCall call) {
		byte[] buffer = serializer.serialize(call);
		int length = buffer.length;
		RpcObject request = new RpcObject(INVOKE,this.genIndex(),length,buffer);
		RpcCallSync sync = new RpcCallSync(request.getIndex(),request);
		rpcCache.put(this.genRpcCallCacheKey(request.getThreadId(), request.getIndex()), sync);
		connector.sendRpcObject(request, timeout);
		clientRpcSync.waitForResult(timeout, sync);
		rpcCache.remove(sync.getIndex());
		RpcObject response = sync.getResponse();
		if(response==null){
			throw new RpcException("null rpc response");
		}
		if(response.getType()==RpcType.FAIL){
			String message = "remote rpc call failed";
			if(response.getLength()>0){
				message = new String(response.getData());
			}
			throw new RpcException(message);
		}
		if(response.getLength()>0){
			return serializer.deserialize(sync.getResponse().getData());
		}
		return null;
	}
	
	@Override
	public void onRpcMessage(RpcObject rpc,RpcSend sender) {
		RpcCallSync sync = rpcCache.get(this.genRpcCallCacheKey(rpc.getThreadId(), rpc.getIndex()));
		if(sync!=null&&sync.getRequest().getThreadId()==rpc.getThreadId()){
			clientRpcSync.notifyResult(sync, rpc);
		}
	}
	
	private int genIndex(){
		return index.getAndIncrement();
	}

	public RpcConnector getConnector() {
		return connector;
	}

	public void setConnector(RpcConnector connector) {
		this.connector = connector;
	}

	@Override
	public void startService() {
		connector.startService();
	}

	@Override
	public void stopService() {
		connector.stopService();
	}
}
