package com.linda.framework.rpc.monitor;

import java.util.List;

public interface RpcMonitorService extends StatMonitor{
	
	public List<RpcMonitorBean> getRpcServices();
	
	public String ping();

}
