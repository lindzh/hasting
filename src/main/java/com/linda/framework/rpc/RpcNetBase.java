package com.linda.framework.rpc;

import java.util.ArrayList;
import java.util.List;

public abstract class RpcNetBase {
	
	public RpcNetBase(){
		callListeners = new ArrayList<RpcCallListener>();
	}
	
	protected List<RpcCallListener> callListeners;
	
	public void addRpcCallListener(RpcCallListener listener){
		callListeners.add(listener);
	}
	
	public void fireCallListeners(RpcObject rpc,RpcSend sender){
		for(RpcCallListener listener:callListeners){
			listener.onRpcMessage(rpc,sender);
		}
	}
	
}
