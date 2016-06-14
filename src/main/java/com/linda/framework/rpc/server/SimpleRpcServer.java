package com.linda.framework.rpc.server;

import com.linda.framework.rpc.nio.AbstractRpcNioSelector;
import com.linda.framework.rpc.nio.SimpleRpcNioSelector;

/**
 * 
 * @author lindezhi
 * 2015年6月14日 上午10:35:03
 */
public class SimpleRpcServer extends AbstractRpcServer{
	
	private AbstractRpcNioSelector nioSelector;

	/**
	 * nio的selector
	 */
	@Override
	public AbstractRpcNioSelector getNioSelector() {
		if(nioSelector==null){
			nioSelector = new SimpleRpcNioSelector();
		}
		return nioSelector;
	}


}
