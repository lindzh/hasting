package com.linda.framework.rpc.monitor;

import java.util.List;

import com.linda.framework.rpc.RpcService;

public interface RpcMonitorService extends StatMonitor{
	
	public List<RpcService> getRpcServices();
	
	public String ping();

}
