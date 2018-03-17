package com.lindzh.hasting.cluster;

import com.lindzh.hasting.rpc.cluster1.RpcHostAndPort;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RpcClusterUtils {
	
	public static Set<String> toString(List<RpcHostAndPort> hostAndPorts){
		Set<String> set = new HashSet<String>();
		for(RpcHostAndPort hap:hostAndPorts){
			set.add(hap.toString());
		}
		return set;
	}

	public static RpcHostAndPort genConsumerInfo(String application,String ip){
		RpcHostAndPort rpcHostAndPort = new RpcHostAndPort();
		rpcHostAndPort.setToken("simple");
		rpcHostAndPort.setApplication(application);
		rpcHostAndPort.setWeight(100);
		rpcHostAndPort.setPort(100);
		rpcHostAndPort.setHost(ip);
		rpcHostAndPort.setTime(System.currentTimeMillis());
		return rpcHostAndPort;
	}

}
