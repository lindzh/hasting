package com.lindzh.hasting.rpc.sync;

import com.lindzh.hasting.rpc.RpcCallSync;
import com.lindzh.hasting.rpc.RpcObject;

/**
 * 同步抽象类
 * @author lindezhi
 * 2016年3月9日 上午11:32:50
 */
public interface RpcSync {
	
	/**
	 * 同步等待执行结果
	 * @param time
	 * @param sync
	 */
	public void waitForResult(int time,RpcCallSync sync);
	
	/**
	 * 通知结果返回
	 * @param sync
	 * @param rpc 返回值
	 */
	public void notifyResult(RpcCallSync sync,RpcObject rpc);
}
