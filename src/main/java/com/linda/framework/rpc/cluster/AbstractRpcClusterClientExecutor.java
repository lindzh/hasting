package com.linda.framework.rpc.cluster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.linda.framework.rpc.RemoteCall;
import com.linda.framework.rpc.RpcService;
import com.linda.framework.rpc.client.AbstractClientRemoteExecutor;
import com.linda.framework.rpc.exception.RpcException;
import com.linda.framework.rpc.generic.GenericService;
import com.linda.framework.rpc.net.AbstractRpcConnector;
import com.linda.framework.rpc.net.RpcNetBase;
import com.linda.framework.rpc.net.RpcNetListener;
import com.linda.framework.rpc.utils.RpcUtils;

/**
 * 
 * @author lindezhi
 * rpc 客户端调用方执行器，及时更新rpc服务器列表和rpc列表
 */
public abstract class AbstractRpcClusterClientExecutor extends AbstractClientRemoteExecutor implements RpcNetListener{
	
	public abstract List<RpcHostAndPort> getHostAndPorts();
	
	public abstract List<RpcService> getServerService(RpcHostAndPort hostAndPort);
	
	public abstract void startRpcCluster();
	
	public abstract void stopRpcCluster();
	
	public abstract String hash(List<String> servers);
	
	public abstract void onClose(RpcHostAndPort hostAndPort);
	
	private Map<String,List<String>> serviceServerCache = new HashMap<String,List<String>>();

	private Map<String,RpcHostAndPort> serverHostCache = new HashMap<>();
	
	private Map<String,AbstractRpcConnector> serverConnectorCache = new HashMap<String,AbstractRpcConnector>();
	
	private Logger logger = Logger.getLogger(AbstractRpcClusterClientExecutor.class);
	
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

	private String genserviceServersKey(String group,String name,String version){
		return group+":"+name+":"+version;
	}

	protected boolean startConnector(RpcHostAndPort hostAndPort){
		try{
			boolean initAndStartConnector = this.initAndStartConnector(hostAndPort);
			if(initAndStartConnector){
				List<RpcService> serverServices = this.getServerService(hostAndPort);
				if(serverServices!=null){
					for(RpcService serverService:serverServices){
						String key = this.genserviceServersKey(serverService.getGroup(),serverService.getName(),serverService.getVersion());
						List<String> servers = serviceServerCache.get(key);
						if(servers==null){
							servers = new ArrayList<String>();
							serviceServerCache.put(key, servers);
						}
						servers.add(hostAndPort.toString());
					}
				}
			}
			return initAndStartConnector;
		}catch(Exception e){
			logger.error("connect to "+hostAndPort.toString()+" error:"+e.getMessage());
			return false;
		}
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
			this.startConnector(hostAndPort);
		}
	}
	
	/**
	 * 初始化 启动connector
	 * @param hostAndPort
	 * @return
	 */
	private boolean initAndStartConnector(RpcHostAndPort hostAndPort){
		AbstractRpcConnector rpcConnector = serverConnectorCache.get(hostAndPort.toString());
		if(rpcConnector!=null){
			return false;
		}
		AbstractRpcConnector connector = RpcUtils.createConnector(connectorClass);
		connector.setHost(hostAndPort.getHost());
		connector.setPort(hostAndPort.getPort());
		connector.addRpcCallListener(this);
		connector.addRpcNetListener(this);
		connector.startService();

		serverHostCache.put(hostAndPort.toString(),hostAndPort);
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
		serverHostCache.remove(server);
		Collection<List<String>> values = serviceServerCache.values();
		for(List<String> servers:values){
			if(servers!=null){
				servers.remove(server);
			}
		}
	}
	
	@Override
	public void onClose(RpcNetBase network, Exception e) {
		this.removeServer(network.getHost()+":"+network.getPort());
		this.onClose(new RpcHostAndPort(network.getHost(),network.getPort()));
	}

	@Override
	public AbstractRpcConnector getRpcConnector(RemoteCall call) {
		List<String> servers = Collections.emptyList();
		//泛型每台服务器都会有，所以需要转换server，做过滤处理
		if(call.getService().equals(GenericService.class.getCanonicalName())){
			String group = (String)call.getArgs()[0];
			String service = (String)call.getArgs()[1];
			String version = (String)call.getArgs()[2];
			String key = this.genserviceServersKey(group,service,version);
			servers = serviceServerCache.get(key);
		}else{
			String key = this.genserviceServersKey(call.getGroup(),call.getService(),call.getVersion());
			servers = serviceServerCache.get(key);
		}
		if(servers==null||servers.size()<1){
			throw new RpcException("can't find server for:"+call);
		}
		AbstractRpcConnector connector = null;
		RpcHostAndPort hostAndPort = null;
		while(connector==null&&servers.size()>0){
			String server = this.hash(servers);
			connector = serverConnectorCache.get(server);
			hostAndPort = serverHostCache.get(server);
		}
		if(connector==null){
			throw new RpcException("can't find connector for:"+call);
		}

		//加入token
		if(hostAndPort!=null){
			call.getAttachment().put("RpcToken",hostAndPort.getToken());
		}
		return connector;
	} 
}
