package com.linda.framework.rpc.monitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.linda.framework.rpc.RpcServiceBean;
import com.linda.framework.rpc.server.RpcServicesHolder;

public class RpcMonitorServiceImpl implements RpcMonitorService{
	
	private RpcServicesHolder rpcServicesHolder;
	
	public RpcMonitorServiceImpl(RpcServicesHolder rpcServicesHolder){
		this.rpcServicesHolder = rpcServicesHolder;
	}

	@Override
	public List<RpcMonitorBean> getRpcServices() {
		if(rpcServicesHolder!=null){
			List<RpcServiceBean> services = rpcServicesHolder.getRpcServices();
			if(services!=null&&services.size()>0){
				List<RpcMonitorBean> list = new ArrayList<RpcMonitorBean>();
				for(RpcServiceBean service:services){
					list.add(new RpcMonitorBean(service.getInterf().getName(),service.getVersion(),service.getBean().getClass().getName()));
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
}
