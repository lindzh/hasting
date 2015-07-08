package com.linda.framework.rpc.monitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.linda.framework.rpc.RpcService;
import com.linda.framework.rpc.RpcServiceBean;
import com.linda.framework.rpc.server.RpcServicesHolder;

public class RpcMonitorServiceImpl implements RpcMonitorService{
	
	private RpcServicesHolder rpcServicesHolder;
	
	private StatMonitor statMonitor;
	
	private long time = 0;
	
	public RpcMonitorServiceImpl(RpcServicesHolder rpcServicesHolder,StatMonitor statMonitor){
		this.rpcServicesHolder = rpcServicesHolder;
		time = System.currentTimeMillis();
	}

	@Override
	public List<RpcService> getRpcServices() {
		if(rpcServicesHolder!=null){
			List<RpcServiceBean> services = rpcServicesHolder.getRpcServices();
			if(services!=null&&services.size()>0){
				List<RpcService> list = new ArrayList<RpcService>();
				for(RpcServiceBean service:services){
					RpcService rpcService = new RpcService(service.getInterf().getName(),service.getVersion(),service.getBean().getClass().getName());
					rpcService.setTime(time);
					list.add(rpcService);
				}
				return list;
			}
		}
		return Collections.emptyList();
	}

	@Override
	public String ping() {
		return "pong";
	}

	@Override
	public Map<Long, Long> getRpcStat() {
		if(statMonitor!=null){
			return statMonitor.getRpcStat();
		}
		return new HashMap<Long, Long>();
	}
}
