package com.lindzh.hasting.cluster.zk;

import com.lindzh.hasting.rpc.client.AbstractClientRemoteExecutor;
import com.lindzh.hasting.rpc.cluster1.RpcClusterClient;

public class ZkRpcClient extends RpcClusterClient {

	private ZkRpcClientExecutor zkRpcClientExecutor;
	
	private String namespace = "rpc";
	
	private String connectString;
	
	private int connectTimeout = 8000;
	
	private int maxRetry = 5;
	
	private int baseSleepTime = 1000;

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getConnectString() {
		return connectString;
	}

	public void setConnectString(String connectString) {
		this.connectString = connectString;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public int getMaxRetry() {
		return maxRetry;
	}

	public void setMaxRetry(int maxRetry) {
		this.maxRetry = maxRetry;
	}

	public int getBaseSleepTime() {
		return baseSleepTime;
	}

	public void setBaseSleepTime(int baseSleepTime) {
		this.baseSleepTime = baseSleepTime;
	}

	@Override
	public <T> T register(Class<T> iface, String version, String group) {
		this.checkExecutor();
		return super.register(iface, version, group);
	}

	private void checkExecutor(){
		if(zkRpcClientExecutor==null){
			zkRpcClientExecutor = new ZkRpcClientExecutor();
			if(this.namespace!=null){
				zkRpcClientExecutor.setNamespace(namespace);
			}
			zkRpcClientExecutor.setBaseSleepTime(baseSleepTime);
			zkRpcClientExecutor.setConnectString(connectString);
			zkRpcClientExecutor.setConnectTimeout(connectTimeout);
			zkRpcClientExecutor.setMaxRetry(maxRetry);
		}
		super.setRemoteExecutor(zkRpcClientExecutor);
	}

	@Override
	public AbstractClientRemoteExecutor getRemoteExecutor() {
		this.checkExecutor();
		return zkRpcClientExecutor;
	}
}
