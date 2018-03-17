package com.lindzh.hasting.cluster.admin;

import java.util.List;

import com.lindzh.hasting.rpc.RpcService;
import com.lindzh.hasting.rpc.Service;
import com.lindzh.hasting.rpc.cluster1.ConsumeRpcObject;
import com.lindzh.hasting.rpc.cluster1.RpcHostAndPort;
import com.lindzh.hasting.rpc.cluster1.HostWeight;
import com.lindzh.hasting.cluster.limit.LimitDefine;
import com.lindzh.hasting.rpc.serializer.RpcSerializer;

public abstract class RpcAdminService implements Service{
	
	public abstract List<RpcHostAndPort> getRpcServers();
	
	public abstract List<RpcService> getRpcServices(RpcHostAndPort rpcServer);
	
	public abstract String getNamespace();
	
	public abstract void setNamespace(String namespace);
	
	public abstract void setSerializer(RpcSerializer serializer);

	/**
	 * 获取权重列表
	 * @param application
	 * @return
	 */
	public abstract List<HostWeight> getWeights(String application);

	/**
	 * 设置权重列表
	 * @param application
	 * @param weight
	 */
	public abstract void setWeight(String application,HostWeight weight);

	public abstract List<ConsumeRpcObject> getConsumers(String group, String service, String version);

	public abstract void setLimits(String application,List<LimitDefine> limits);

}
