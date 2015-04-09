package com.linda.framework.rpc.cluster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.linda.framework.rpc.RemoteCall;
import com.linda.framework.rpc.client.AbstractClientRemoteExecutor;
import com.linda.framework.rpc.exception.RpcException;
import com.linda.framework.rpc.generic.GenericService;
import com.linda.framework.rpc.net.AbstractRpcConnector;
import com.linda.framework.rpc.utils.RpcUtils;

public abstract class AbstractRpcClusterClientExecutor extends AbstractClientRemoteExecutor{
	
	public abstract List<RpcHostAndPort> getHostAndPorts();
	
	public abstract List<ServiceAndVersion> getServerService(RpcHostAndPort hostAndPort);
	
	public abstract void startRpcCluster();
	
	public abstract void stopRpcCluster();
	
	public abstract String hash(List<String> servers);
	
	private Map<String,List<String>> serviceServerCache = new HashMap<String,List<String>>();
	
	private Map<String,AbstractRpcConnector> serverConnectorCache = new HashMap<String,AbstractRpcConnector>();
	
	private Class connectorClass;
	
	public Class getConnectorClass() {
		return connectorClass;
	}

	public void setConnectorClass(Class connectorClass) {
		this.connectorClass = connectorClass;
	}

	@Override
	public void startService() {
		this.startRpcCluster();
		this.startConnectors();
	}
	
	/**
	 * 启动集群，并加入
	 */
	private void startConnectors(){
		List<RpcHostAndPort> hostAndPorts = this.getHostAndPorts();
		if(hostAndPorts==null){
			throw new RpcException("can't find any server");
		}
		for(RpcHostAndPort hostAndPort:hostAndPorts){
			boolean initAndStartConnector = this.initAndStartConnector(hostAndPort);
			if(initAndStartConnector){
				List<ServiceAndVersion> serverServices = this.getServerService(hostAndPort);
				if(serverServices!=null){
					for(ServiceAndVersion serverService:serverServices){
						List<String> servers = serviceServerCache.get(serverService.toString());
						if(servers==null){
							servers = new ArrayList<String>();
							serviceServerCache.put(serverService.toString(), servers);
						}
						servers.add(hostAndPort.toString());
					}
				}
			}
		}
	}
	
	/**
	 * 初始化 启动connector
	 * @param hostAndPort
	 * @return
	 */
	private boolean initAndStartConnector(RpcHostAndPort hostAndPort){
		AbstractRpcConnector connector = RpcUtils.createConnector(connectorClass);
		connector.setHost(hostAndPort.getHost());
		connector.setPort(hostAndPort.getPort());
		connector.addRpcCallListener(this);
		connector.startService();
		serverConnectorCache.put(hostAndPort.toString(), connector);
		return true;
	}

	public void remoteConnector(RpcHostAndPort hostAndPort){
		serverConnectorCache.remove(hostAndPort.toString());
	}
	
	/**
	 * 启动服务，先关闭集群，再关闭connector 
	 */
	@Override
	public void stopService() {
		this.stopRpcCluster();
		this.startConnectors();
	}
	
	/**
	 * 删除server，一旦检测到server 宕机
	 * @param server
	 */
	public void removeServer(String server){
		AbstractRpcConnector connector = serverConnectorCache.get(server);
		if(connector!=null){
			connector.stopService();
		}
		serverConnectorCache.remove(server);
		Collection<List<String>> values = serviceServerCache.values();
		for(List<String> servers:values){
			if(servers!=null){
				servers.remove(server);
			}
		}
	}
	
	@Override
	public AbstractRpcConnector getRpcConnector(RemoteCall call) {
		List<String> servers = Collections.emptyList();
		//泛型每台服务器都会有，所以需要转换server，做过滤处理
		if(call.getClass()==GenericService.class){
			String service = (String)call.getArgs()[0];
			String version = (String)call.getArgs()[1];
			servers = serviceServerCache.get(service+":"+version);
		}else{
			servers = serviceServerCache.get(call.getService()+":"+call.getVersion());
		}
		if(servers==null||servers.size()<1){
			throw new RpcException("can't find server for:"+call);
		}
		AbstractRpcConnector connector = null;
		while(connector==null&&servers.size()>0){
			String server = this.hash(servers);
			connector = serverConnectorCache.get(server);
		}
		if(connector==null){
			throw new RpcException("can't find connector for:"+call);
		}
		return connector;
	} 
}
