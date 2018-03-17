package com.lindzh.hasting.rpc.cluster1;

import java.util.*;

import com.lindzh.hasting.rpc.RpcService;
import com.lindzh.hasting.rpc.client.AbstractClientRemoteExecutor;
import org.apache.log4j.Logger;

import com.lindzh.hasting.rpc.RemoteCall;
import com.lindzh.hasting.rpc.exception.RpcException;
import com.lindzh.hasting.rpc.generic.GenericService;
import com.lindzh.hasting.rpc.net.AbstractRpcConnector;
import com.lindzh.hasting.rpc.net.RpcNetBase;
import com.lindzh.hasting.rpc.net.RpcNetListener;
import com.lindzh.hasting.rpc.utils.RpcUtils;

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

	private String application;

	/**
	 * 本机ip
	 */
	private String selfIp;

	/**
	 * 提供给子类使用,方便提供依赖上报
	 * @param iface
	 * @param version
	 * @param group
     * @param <T>
     */
	public abstract <T> void doRegisterRemote(String application,Class<T> iface, String version, String group);
	
	public abstract String hash(List<RpcHostAndPort> servers);
	
	public abstract void onClose(RpcHostAndPort hostAndPort);
	
	private Map<String,List<RpcHostAndPort>> serviceServerCache = new HashMap<String,List<RpcHostAndPort>>();

	private Map<String,RpcService> serviceCache = new HashMap<String,RpcService>();

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
		//消费者上传不加入
		if(hostAndPort.getPort()<1000){
			return false;
		}
		//消费者
		try{
			boolean initAndStartConnector = this.initAndStartConnector(hostAndPort);
			if(initAndStartConnector){
				List<RpcService> serverServices = this.getServerService(hostAndPort);
				if(serverServices!=null){
					for(RpcService serverService:serverServices){
						String key = this.genserviceServersKey(serverService.getGroup(),serverService.getName(),serverService.getVersion());
						//加入,方便权重使用
						serviceCache.put(key,serverService);
						List<RpcHostAndPort> servers = serviceServerCache.get(key);
						if(servers==null){
							servers = new ArrayList<RpcHostAndPort>();
							serviceServerCache.put(key, servers);
						}
						servers.add(hostAndPort);
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
		serverHostCache.remove(hostAndPort.toString());
	}
	
	/**
	 * 启动服务，先关闭集群，再关闭connector 
	 */
	@Override
	public void stopService() {
		this.stopRpcCluster();
		this.stopConnectors();
	}

	/**
	 * 停止服务
	 */
	private void stopConnectors(){
		Set<Map.Entry<String, AbstractRpcConnector>> entries = serverConnectorCache.entrySet();
		for(Map.Entry<String, AbstractRpcConnector> entry:entries){
			AbstractRpcConnector connector = entry.getValue();
			if(connector!=null){
				connector.stopService();
			}
		}
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
		Set<String> keys = serviceServerCache.keySet();
		for(String key:keys){
			List<RpcHostAndPort> servers = serviceServerCache.get(key);

			ArrayList<RpcHostAndPort> hostAndPorts = new ArrayList<>();
			for(RpcHostAndPort ss:servers){
				if(!server.equals(ss.toString())){
					hostAndPorts.add(ss);
				}
			}
			serviceServerCache.put(key,hostAndPorts);
		}
	}
	
	@Override
	public void onClose(RpcNetBase network, Exception e) {
		this.removeServer(network.getHost()+":"+network.getPort());
		this.onClose(new RpcHostAndPort(network.getHost(),network.getPort()));
	}

	@Override
	public AbstractRpcConnector getRpcConnector(RemoteCall call) {
		List<RpcHostAndPort> servers = Collections.emptyList();
		String application = null;
		//泛型每台服务器都会有，所以需要转换server，做过滤处理
		if(call.getService().equals(GenericService.class.getCanonicalName())){
			String group = (String)call.getArgs()[0];
			String service = (String)call.getArgs()[1];
			String version = (String)call.getArgs()[2];
			String key = this.genserviceServersKey(group,service,version);
			RpcService rpcService = serviceCache.get(key);
			if(rpcService!=null){
				application = rpcService.getApplication();
				servers = serviceServerCache.get(key);
			}
		}else{
			String key = this.genserviceServersKey(call.getGroup(),call.getService(),call.getVersion());
			RpcService rpcService = serviceCache.get(key);
			if(rpcService!=null){
				application = rpcService.getApplication();
				servers = serviceServerCache.get(key);
			}
		}
		if(application==null||servers==null||servers.size()<1){
			throw new RpcException("can't find server for:"+call);
		}
		AbstractRpcConnector connector = null;
		RpcHostAndPort hostAndPort = null;

		while(connector==null&&servers.size()>0){

			//获得权重值
			List<HostWeight> weights = this.getWeights(application);
			this.setWeight(weights,servers);

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

	private void setWeight(List<HostWeight> weights,List<RpcHostAndPort> hosts){
		Map<String,HostWeight> weightMap = new HashMap<>();
		for(HostWeight ww:weights){
			weightMap.put(ww.getKey(),ww);
		}

		for(RpcHostAndPort host:hosts){
			HostWeight weight = weightMap.get(host.toString());
			if(weight!=null){
				host.setWeight(weight.getWeight());
			}
		}
	}

	public String getSelfIp() {
		if(selfIp==null){
			selfIp = RpcUtils.chooseIP(RpcUtils.getLocalV4IPs());
		}
		return selfIp;
	}

	public void setSelfIp(String selfIp) {
		this.selfIp = selfIp;
	}

	/**
	 * 消费者应用列表
	 * @param group
	 * @param service
	 * @param version
     * @return
     */
	public abstract List<String> getConsumeApplications(String group,String service,String version);

	/**
	 * 获取消费者机器列表
	 * @param group
	 * @param service
	 * @param version
     * @return
     */
	public abstract List<ConsumeRpcObject> getConsumeObjects(String group,String service,String version);

	/**
	 * 获取权重列表
	 * @param application
	 * @return
     */
	public abstract List<HostWeight> getWeights(String application);

	/**
	 * 设置权重列表
	 * @param application
	 * @param weight
     */
	public abstract void setWeight(String application,HostWeight weight);

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}
}
