package com.lindzh.hasting.webui.service;

import java.util.List;
import java.util.Map;

import com.lindzh.hasting.rpc.RpcService;
import com.lindzh.hasting.rpc.cluster1.RpcHostAndPort;

public interface RpcInfoListener {
	
	public void onServers(RpcConfig config,List<RpcHostAndPort> host);
	
	public void onServices(RpcConfig config,Map<RpcHostAndPort,List<RpcService>> hostServices);

}
