package com.lindzh.hasting.webui.service;

import java.util.List;

import com.lindzh.hasting.rpc.RpcService;
import com.lindzh.hasting.rpc.cluster1.RpcHostAndPort;

public interface RpcWebuiService {
	
	public RpcConfig getNamespaceConfig(String namespace);
	
	public List<RpcService> search(String namespace,String keyword);
	
	public List<RpcService> getServicesByHost(String namespace,String hostAndPort);
	
	public List<String> getNamespaces();
	
	public List<RpcConfig> getRpcConfigs();
	
	public List<RpcHostAndPort> getHostsByNamespace(String namespace);
	
	public List<RpcHostAndPort> getRpcHostsByRpc(String namespace,String serviceName,String serviceVersion);

}
