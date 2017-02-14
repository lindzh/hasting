package com.linda.framework.rpc.cluster;

import java.util.ArrayList;
import java.util.List;

import com.linda.framework.rpc.RpcService;
import com.linda.framework.rpc.net.RpcNetBase;
import com.linda.framework.rpc.utils.RpcUtils;

public class SimpleClusterExecutor extends AbstractRpcClusterClientExecutor{

	@Override
	public List<RpcHostAndPort> getHostAndPorts() {
		List<RpcHostAndPort> list = new ArrayList<RpcHostAndPort>();
		list.add(new RpcHostAndPort("127.0.0.1",4445));
		return list;
	}

	@Override
	public List<RpcService> getServerService(RpcHostAndPort hostAndPort) {
		List<RpcService> services = new ArrayList<RpcService>();
		services.add(new RpcService("com.linda.framework.rpc.HelloRpcService",RpcUtils.DEFAULT_VERSION));
		services.add(new RpcService("com.linda.framework.rpc.generic.GenericService",RpcUtils.DEFAULT_VERSION));
		return services;
	}

	@Override
	public void startRpcCluster() {
		
	}

	@Override
	public void stopRpcCluster() {
		
	}

	@Override
	public <T> void doRegisterRemote(String application, Class<T> iface, String version, String group) {

	}

	@Override
	public List<String> getConsumeApplications(String group, String service, String version) {
		return null;
	}

	@Override
	public List<ConsumeRpcObject> getConsumeObjects(String group, String service, String version) {
		return null;
	}

	@Override
	public List<HostWeight> getWeights(String application) {
		return null;
	}

	@Override
	public void setWeight(String application, HostWeight weight) {

	}

	@Override
	public void onClose(RpcHostAndPort hostAndPort) {
		System.out.println("close:"+hostAndPort);
	}


	@Override
	public void onStart(RpcNetBase network) {
		System.out.println("start:"+network.getHost()+":"+network.getPort());
	}

	@Override
	public String hash(List<RpcHostAndPort> servers) {
		return null;
	}
}
