package com.lindzh.hasting.cluster.zk;

import java.util.List;

import com.lindzh.hasting.rpc.RpcService;
import com.lindzh.hasting.rpc.Service;
import com.lindzh.hasting.rpc.client.AbstractClientRemoteExecutor;
import com.lindzh.hasting.rpc.cluster1.RpcHostAndPort;
import com.lindzh.hasting.rpc.cluster1.ConsumeRpcObject;
import com.lindzh.hasting.rpc.cluster1.HostWeight;
import com.lindzh.hasting.cluster.admin.RpcAdminService;
import com.lindzh.hasting.cluster.limit.LimitDefine;
import com.lindzh.hasting.rpc.net.AbstractRpcConnector;
import com.lindzh.hasting.rpc.serializer.RpcSerializer;

public class ZkRpcAdminService extends RpcAdminService implements Service  {

	private ZkRpcClient zkRpcClient = new ZkRpcClient();

	public String getNamespace() {
		return zkRpcClient.getNamespace();
	}

	public String getConnectString() {
		return zkRpcClient.getConnectString();
	}

	public void setNamespace(String namespace) {
		zkRpcClient.setNamespace(namespace);
	}

	public void setConnectString(String connectString) {
		zkRpcClient.setConnectString(connectString);
	}

	public Class<? extends AbstractRpcConnector> getConnectorClass() {
		return zkRpcClient.getConnectorClass();
	}

	public void setConnectorClass(Class<? extends AbstractRpcConnector> connectorClass) {
		zkRpcClient.setConnectorClass(connectorClass);
	}

	@Override
	public void startService() {
		this.getExecutor().setAdmin(true);
		this.zkRpcClient.startService();
	}

	@Override
	public void stopService() {
		this.zkRpcClient.stopService();
	}

	private ZkRpcClientExecutor getExecutor() {
		AbstractClientRemoteExecutor executor = zkRpcClient.getRemoteExecutor();
		return (ZkRpcClientExecutor) executor;
	}

	@Override
	public List<RpcHostAndPort> getRpcServers() {
		return this.getExecutor().getHostAndPorts();
	}

	@Override
	public List<RpcService> getRpcServices(RpcHostAndPort rpcServer) {
		return this.getExecutor().getServerService(rpcServer);
	}

	@Override
	public void setSerializer(RpcSerializer serializer) {
		zkRpcClient.setSerializer(serializer);
	}

	@Override
	public List<HostWeight> getWeights(String application) {
		return this.getExecutor().getWeights(application);
	}

	@Override
	public void setWeight(String application, HostWeight weight) {
		this.getExecutor().setWeight(application, weight);
	}

	@Override
	public List<ConsumeRpcObject> getConsumers(String group, String service, String version) {
		return this.getExecutor().getConsumeObjects(group, service, version);
	}

	@Override
	public void setLimits(String application,List<LimitDefine> limits) {
		this.getExecutor().setLimits(application,limits);
	}
}
