package com.lindzh.hasting.rpc.client;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.lindzh.hasting.rpc.RemoteExecutor;
import com.lindzh.hasting.rpc.RpcCallSync;
import com.lindzh.hasting.rpc.RpcObject;
import com.lindzh.hasting.rpc.net.RpcCallListener;
import com.lindzh.hasting.rpc.net.RpcSender;
import com.lindzh.hasting.rpc.sync.RpcSync;
import org.apache.log4j.Logger;

import com.lindzh.hasting.rpc.RemoteCall;
import com.lindzh.hasting.rpc.Service;
import com.lindzh.hasting.rpc.exception.RpcException;
import com.lindzh.hasting.rpc.net.AbstractRpcConnector;
import com.lindzh.hasting.rpc.serializer.JdkSerializer;
import com.lindzh.hasting.rpc.serializer.RpcSerializer;
import com.lindzh.hasting.rpc.sync.SimpleFutureRpcSync;
import com.lindzh.hasting.rpc.utils.RpcUtils.RpcType;

public abstract class AbstractClientRemoteExecutor implements RemoteExecutor,RpcCallListener,Service{
	protected int timeout = 10000;
	private AtomicInteger index = new AtomicInteger(10000);
	private RpcSync clientRpcSync;
	private RpcSerializer serializer;
	
	private Logger logger = Logger.getLogger(AbstractClientRemoteExecutor.class);
	
	public AbstractClientRemoteExecutor(){
		clientRpcSync = new SimpleFutureRpcSync();
		serializer = new JdkSerializer();
	}
	
	private ConcurrentHashMap<String,RpcCallSync> rpcCache = new ConcurrentHashMap<String,RpcCallSync>();
	
	@Override
	public void oneway(RemoteCall call) {
		AbstractRpcConnector connector = getRpcConnector(call);
		byte[] buffer = serializer.serialize(call);
		int length = buffer.length;
		RpcObject rpc = new RpcObject(ONEWAY,this.genIndex(),length,buffer);
		connector.sendRpcObject(rpc, timeout);
	}
	
	private String genRpcCallCacheKey(long threadId,int index){
		return "rpc_"+threadId+"_"+index;
	}

	@Override
	public Object invoke(RemoteCall call) {
		AbstractRpcConnector connector = getRpcConnector(call);
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
	public void onRpcMessage(RpcObject rpc,RpcSender sender) {
		RpcCallSync sync = rpcCache.get(this.genRpcCallCacheKey(rpc.getThreadId(), rpc.getIndex()));
		if(sync!=null&&sync.getRequest().getThreadId()==rpc.getThreadId()){
			clientRpcSync.notifyResult(sync, rpc);
		}
	}
	
	private int genIndex(){
		return index.getAndIncrement();
	}

	public abstract AbstractRpcConnector getRpcConnector(RemoteCall call);

	public RpcSerializer getSerializer() {
		return serializer;
	}

	public void setSerializer(RpcSerializer serializer) {
		this.serializer = serializer;
	}
}
