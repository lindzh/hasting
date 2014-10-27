package com.linda.framework.rpc.sync;

import com.linda.framework.rpc.RpcCallSync;
import com.linda.framework.rpc.RpcObject;

public interface RpcSync {
	
	public void waitForResult(int time,RpcCallSync sync);
	
	public void notifyResult(RpcCallSync sync,RpcObject rpc);
}
