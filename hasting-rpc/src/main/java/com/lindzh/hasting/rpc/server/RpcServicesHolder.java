package com.lindzh.hasting.rpc.server;

import java.util.List;

import com.lindzh.hasting.rpc.RpcServiceBean;

/**
 * provider提供的api列表
 * @author lindezhi
 * 2016年6月14日 上午10:23:33
 */
public interface RpcServicesHolder {
	
	public List<RpcServiceBean> getRpcServices();

}
