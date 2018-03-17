package com.lindzh.hasting.cluster.etcd;

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

public class EtcdRpcAdminService  extends RpcAdminService implements Service  {

	private EtcdRpcClient etcdRpcClient = new EtcdRpcClient();

	public String getNamespace() {
		return etcdRpcClient.getNamespace();
	}

	public void setNamespace(String namespace) {
		etcdRpcClient.setNamespace(namespace);
	}

	public String getEtcdUrl() {
		return etcdRpcClient.getEtcdUrl();
	}

	public void setEtcdUrl(String etcdUrl) {
		etcdRpcClient.setEtcdUrl(etcdUrl);
	}

	public Class<? extends AbstractRpcConnector> getConnectorClass() {
		return etcdRpcClient.getConnectorClass();
	}

	public void setConnectorClass(Class<? extends AbstractRpcConnector> connectorClass) {
		etcdRpcClient.setConnectorClass(connectorClass);
	}

	private EtcdRpcClientExecutor getExecutor() {
		AbstractClientRemoteExecutor executor = etcdRpcClient.getRemoteExecutor();
		return (EtcdRpcClientExecutor) executor;
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
	public void startService() {
		this.getExecutor().setAdmin(true);
		etcdRpcClient.startService();
	}

	@Override
	public void stopService() {
		etcdRpcClient.stopService();
	}

	@Override
	public void setSerializer(RpcSerializer serializer) {
		etcdRpcClient.setSerializer(serializer);
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
