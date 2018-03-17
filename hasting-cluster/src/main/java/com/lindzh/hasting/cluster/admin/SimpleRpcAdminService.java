package com.lindzh.hasting.cluster.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.lindzh.hasting.rpc.RpcService;
import com.lindzh.hasting.rpc.Service;
import com.lindzh.hasting.rpc.client.SimpleRpcClient;
import com.lindzh.hasting.rpc.cluster1.ConsumeRpcObject;
import com.lindzh.hasting.rpc.cluster1.HostWeight;
import com.lindzh.hasting.rpc.cluster1.RpcHostAndPort;
import com.lindzh.hasting.cluster.limit.LimitDefine;
import com.lindzh.hasting.rpc.monitor.RpcMonitorService;
import com.lindzh.hasting.rpc.net.AbstractRpcConnector;
import com.lindzh.hasting.rpc.serializer.RpcSerializer;

public class SimpleRpcAdminService extends RpcAdminService implements Service {

	private SimpleRpcClient client = new SimpleRpcClient();

	private RpcMonitorService monitorService;

	private List<RpcHostAndPort> hosts = new ArrayList<RpcHostAndPort>();

	public Class<? extends AbstractRpcConnector> getConnectorClass() {
		return client.getConnectorClass();
	}

	public void setConnectorClass(Class<? extends AbstractRpcConnector> connectorClass) {
		client.setConnectorClass(connectorClass);
	}

	public String getHost() {
		return client.getHost();
	}

	public void setHost(String host) {
		client.setHost(host);
	}

	public int getPort() {
		return client.getPort();
	}

	public void setPort(int port) {
		client.setPort(port);
	}

	@Override
	public void startService() {
		client.startService();
		RpcHostAndPort hostAndPort = new RpcHostAndPort();
		hostAndPort.setHost(client.getHost());
		hostAndPort.setPort(client.getPort());
		hostAndPort.setTime(System.currentTimeMillis());
		hosts.add(hostAndPort);
		monitorService = client.register(RpcMonitorService.class);
	}

	@Override
	public void stopService() {
		client.stopService();
	}

	@Override
	public List<RpcHostAndPort> getRpcServers() {
		return hosts;
	}

	@Override
	public List<RpcService> getRpcServices(RpcHostAndPort rpcServer) {
		return monitorService.getRpcServices();
	}

	@Override
	public String getNamespace() {
		return null;
	}

	@Override
	public void setNamespace(String namespace) {
		
	}

	@Override
	public void setSerializer(RpcSerializer serializer) {
		client.setSerializer(serializer);
	}

	@Override
	public List<HostWeight> getWeights(String application) {
		List<HostWeight> list = new ArrayList<HostWeight>();
		if(hosts.size()>0){
			for(RpcHostAndPort host:hosts){
				HostWeight hostWeight = new HostWeight();
				hostWeight.setHost(host.getHost());
				hostWeight.setPort(host.getPort());
				hostWeight.setWeight(100);
				list.add(hostWeight);
			}
		}
		return list;
	}

	@Override
	public void setWeight(String application, HostWeight weight) {
		//do nothing
	}

	@Override
	public List<ConsumeRpcObject> getConsumers(String group, String service, String version) {
		return Collections.emptyList();
	}

	@Override
	public void setLimits(String application,List<LimitDefine> limits) {

	}
}
