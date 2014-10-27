package com.linda.framework.rpc.sync;

import com.linda.framework.rpc.RpcCallSync;
import com.linda.framework.rpc.RpcObject;
import com.linda.framework.rpc.exception.RpcException;

public class SimpleJdkRpcSync implements RpcSync {

	@Override
	public void waitForResult(int time, RpcCallSync sync) {
		synchronized(sync){
			try {
				sync.wait(time);
				if(sync.getResponse()==null){
					throw new RpcException("rpc request time out");
				}
			} catch (InterruptedException e) {
				throw new RpcException(e);
			}
		}
	}

	@Override
	public void notifyResult(RpcCallSync sync, RpcObject rpc) {
		if(sync!=null){
			synchronized(sync){
				sync.setResponse(rpc);
				sync.notify();
			}
		}
	}
}
