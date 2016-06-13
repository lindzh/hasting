package com.linda.framework.rpc.monitor;

import java.util.Map;

/**
 * 统计监控值
 * @author lindezhi
 * 2016年6月13日 下午4:38:04
 */
public interface StatMonitor {

	public Map<Long,Long> getRpcStat();
	
}
