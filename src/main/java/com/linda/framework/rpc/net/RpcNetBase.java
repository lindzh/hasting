package com.linda.framework.rpc.net;

import java.util.LinkedList;
import java.util.List;

import com.linda.framework.rpc.RpcObject;
import com.linda.framework.rpc.Service;

public abstract class RpcNetBase {
	
	public RpcNetBase(){
		callListeners = new LinkedList<RpcCallListener>();
	}
	
	protected List<RpcCallListener> callListeners;
	
	public void addRpcCallListener(RpcCallListener listener){
		callListeners.add(listener);
	}
	
	public void fireCallListeners(RpcObject rpc,RpcSender sender){
		for(RpcCallListener listener:callListeners){
			listener.onRpcMessage(rpc,sender);
		}
	}
	
	protected void startListeners(){
		for(RpcCallListener listener:callListeners){
			if(listener instanceof Service){
				Service service = (Service)listener;
				service.startService();
			}
		}
	}
	
	protected void stopListeners(){
		for(RpcCallListener listener:callListeners){
			if(listener instanceof Service){
				Service service = (Service)listener;
				service.stopService();
			}
		}
	}
	
	protected void addConnectorListeners(AbstractRpcConnector connector){
		for(RpcCallListener listener:callListeners){
			connector.addRpcCallListener(listener);
		}
	}
	
}
