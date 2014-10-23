package com.linda.framework.rpc;

public class SimpleJdkRpcSync implements RpcSync {

	@Override
	public void waitForResult(int time, RpcCallSync sync) {
		synchronized(sync){
			try {
				sync.wait(time);
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
