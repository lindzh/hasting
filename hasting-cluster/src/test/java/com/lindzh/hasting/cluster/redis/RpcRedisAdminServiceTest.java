package com.lindzh.hasting.cluster.redis;

import java.util.List;

import com.lindzh.hasting.rpc.RpcService;
import com.lindzh.hasting.rpc.cluster1.ConsumeRpcObject;
import com.lindzh.hasting.rpc.cluster1.HostWeight;
import com.lindzh.hasting.cluster.JSONUtils;
import com.lindzh.hasting.rpc.cluster1.RpcHostAndPort;
import com.lindzh.hasting.rpc.utils.RpcUtils;

public class RpcRedisAdminServiceTest {
	
	public static void main(String[] args) {
		
		RedisRpcAdminService adminService = new RedisRpcAdminService();
		adminService.setRedisHost("127.0.0.1");
		adminService.setRedisPort(6379);
		adminService.startService();
		List<RpcHostAndPort> rpcServers = adminService.getRpcServers();
		System.out.println("servers start");
		System.out.println(JSONUtils.toJSON(rpcServers));
		System.out.println("servers end");
		for(RpcHostAndPort hap:rpcServers){
			List<RpcService> services = adminService.getRpcServices(hap);
			System.out.println(JSONUtils.toJSON(services));
		}

		HostWeight weight = new HostWeight();
		weight.setWeight(80);
		weight.setHost("172.17.9.251");
		weight.setPort(3322);
		adminService.setWeight("myapp",weight);

		List<HostWeight> weights = adminService.getWeights("myapp");

		System.out.println(JSONUtils.toJSON(weights));

		List<ConsumeRpcObject> consumers = adminService.getConsumers(RpcUtils.DEFAULT_GROUP, "LoginRpcService", RpcUtils.DEFAULT_VERSION);

		System.out.println("consumers--------:"+JSONUtils.toJSON(consumers));
	}
}
