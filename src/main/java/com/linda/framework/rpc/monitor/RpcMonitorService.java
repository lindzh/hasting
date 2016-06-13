package com.linda.framework.rpc.monitor;

import java.util.List;

import com.linda.framework.rpc.RpcService;

/**
 * 服务提供者性能监控，provider内置
 * @author lindezhi
 * 2014年6月13日 下午4:48:32
 */
public interface RpcMonitorService extends StatMonitor{
	
	/**
	 * 获取服务列表
	 * @return
	 */
	public List<RpcService> getRpcServices();
	
	/**
	 * 检测服务是否正常
	 * @return
	 */
	public String ping();

}
