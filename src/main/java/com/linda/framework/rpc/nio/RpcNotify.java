package com.linda.framework.rpc.nio;

import java.nio.channels.SelectionKey;

public interface RpcNotify {

	public void sendNotify(SelectionKey key);
	
}
