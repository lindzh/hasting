package com.lindzh.hasting.cluster.zk;

import java.util.List;

import com.lindzh.hasting.rpc.RpcService;
import com.lindzh.hasting.rpc.cluster1.ConsumeRpcObject;
import com.lindzh.hasting.rpc.cluster1.RpcHostAndPort;
import com.lindzh.hasting.rpc.cluster1.HostWeight;
import com.lindzh.hasting.cluster.JSONUtils;
import com.lindzh.hasting.rpc.utils.RpcUtils;

public class RpcZkAdminTest {
	
	public static void main(String[] args) {
		ZkRpcAdminService adminService = new ZkRpcAdminService();
		adminService.setConnectString("127.0.0.1:2181");
		adminService.setNamespace("myrpc");
		adminService.startService();

		List<RpcHostAndPort> rpcServers = adminService.getRpcServers();
		
		System.out.println(JSONUtils.toJSON(rpcServers));

//		setWeight(adminService);
		
		for(RpcHostAndPort server:rpcServers){
			List<RpcService> services = adminService.getRpcServices(server);
			System.out.println(JSONUtils.toJSON(server.getHost()+":"+server.getPort()+"     "+services));
		}

		List<HostWeight> weights = adminService.getWeights("myapp");
		System.out.println(JSONUtils.toJSON(weights));

		List<ConsumeRpcObject> consumers = adminService.getConsumers("hello", "HelloRpcTestService", RpcUtils.DEFAULT_VERSION);

		System.out.println("consumers:"+JSONUtils.toJSON(consumers));

		consumers = adminService.getConsumers("hello", "HelloRpcService", RpcUtils.DEFAULT_VERSION);

		System.out.println("consumers:"+JSONUtils.toJSON(consumers));

		rpcServers = adminService.getRpcServers();

		System.out.println(JSONUtils.toJSON(rpcServers));

		rpcServers = adminService.getRpcServers();

		System.out.println(JSONUtils.toJSON(rpcServers));

//		adminService.stopService();
	}

	public static void setWeight(ZkRpcAdminService adminService){
		HostWeight weight = new HostWeight();
		weight.setWeight(50);
		weight.setHost("127.0.0.1");
		weight.setPort(3333);
		adminService.setWeight("myapp",weight);
	}

}
