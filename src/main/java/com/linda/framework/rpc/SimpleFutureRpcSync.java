package com.linda.framework.rpc;

public class SimpleFutureRpcSync implements RpcSync{

	@Override
	public void waitForResult(int time, RpcCallSync sync) {
		int timeAll = 0;
		while(!sync.isDone()){
			try {
				Thread.currentThread().sleep(5);
				timeAll+=5;
				if(timeAll>time){
					throw new RpcException("request time out");
				}
			} catch (InterruptedException e) {
				throw new RpcException(e);
			}
		}
	}

	@Override
	public void notifyResult(RpcCallSync sync, RpcObject rpc) {
		if(sync!=null){
			sync.setResponse(rpc);
		}
	}
}
