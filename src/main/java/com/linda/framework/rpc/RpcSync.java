package com.linda.framework.rpc;

public interface RpcSync {
	
	public void waitForResult(int time,RpcCallSync sync);
	
	public void notifyResult(RpcCallSync sync,RpcObject rpc);
}
