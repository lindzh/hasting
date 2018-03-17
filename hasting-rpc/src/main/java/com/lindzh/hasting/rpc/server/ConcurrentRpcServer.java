package com.lindzh.hasting.rpc.server;

import com.lindzh.hasting.rpc.nio.AbstractRpcNioSelector;
import com.lindzh.hasting.rpc.nio.ConcurrentRpcNioSelector;

/**
 * 可以使用多个连接的rpcserver
 * @author lindezhi
 * 2016年6月14日 上午10:35:25
 */
public class ConcurrentRpcServer extends AbstractRpcServer{

	private AbstractRpcNioSelector nioSelector;
	
	@Override
	public AbstractRpcNioSelector getNioSelector() {
		if(nioSelector==null){
			nioSelector = new ConcurrentRpcNioSelector();
		}
		return nioSelector;
	}

}
