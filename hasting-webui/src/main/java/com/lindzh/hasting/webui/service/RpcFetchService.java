package com.lindzh.hasting.webui.service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.lindzh.hasting.rpc.RpcService;
import com.lindzh.hasting.rpc.Service;
import com.lindzh.hasting.cluster.JSONUtils;
import com.lindzh.hasting.rpc.cluster1.RpcHostAndPort;
import com.lindzh.hasting.cluster.admin.RpcAdminService;
import com.lindzh.hasting.cluster.admin.SimpleRpcAdminService;
import com.lindzh.hasting.cluster.etcd.EtcdRpcAdminService;
import com.lindzh.hasting.cluster.redis.RedisRpcAdminService;
import com.lindzh.hasting.cluster.zk.ZkRpcAdminService;
import com.lindzh.hasting.rpc.exception.RpcException;
import com.lindzh.hasting.webui.service.RpcConfig.RpcProtocol;

public class RpcFetchService implements Service{
	
	private List<RpcConfig> rpcConfiguration;
	
	private ConcurrentHashMap<RpcConfig, RpcAdminService> configAdminCache = new ConcurrentHashMap<RpcConfig, RpcAdminService>();
	
	private Logger logger = Logger.getLogger(RpcFetchService.class);

	private List<RpcInfoListener> infoListeners = new ArrayList<RpcInfoListener>();
	
	private Timer timer = new Timer();
	
	private long fetchInterval = 10000;//10s
	
	private ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(5);
	
	public void setRpcConfigs(List<RpcConfig> configs){
		this.rpcConfiguration = configs;
	}
	
	public List<RpcConfig> getRpcConfigs(){
		return this.rpcConfiguration;
	}
	
	private TimerTask fetchTask = new TimerTask(){
		public void run() {
			RpcFetchService.this.fetServerAndServices();
		}
	};
	
	public void addInfoListener(RpcInfoListener listener){
		this.infoListeners.add(listener);
	}
	
	private void fireServerListeners(RpcConfig config,List<RpcHostAndPort> host){
		for(RpcInfoListener listener:this.infoListeners){
			listener.onServers(config, host);
		}
	}
	
	private void fireServicesListeners(RpcConfig config,Map<RpcHostAndPort, List<RpcService>> hashMap){
		for(RpcInfoListener listener:this.infoListeners){
			listener.onServices(config, hashMap);
		}
	}
	
	@Override
	public void startService() {
		logger.info("start fetch service");
		this.startAdminService();
		this.startFetchTask();
	}
	
	private void startFetchTask(){
		timer.scheduleAtFixedRate(fetchTask, 100, fetchInterval);
		logger.info("fetch timer task started");
	}
	
	private void fetServerAndServices(){
		Set<RpcConfig> configs = configAdminCache.keySet();
		for(final RpcConfig config:configs){
			threadPoolExecutor.submit(new Runnable(){
				@Override
				public void run() {
					RpcAdminService adminService = configAdminCache.get(config);
					if(adminService==null){
						RpcFetchService.this.initConfig(config);
					}else{
						HashMap<RpcHostAndPort, List<RpcService>> hashMap = new HashMap<RpcHostAndPort, List<RpcService>>();
						List<RpcHostAndPort> servers = adminService.getRpcServers();
						if(servers!=null){
							logger.info("fetchServer config:"+JSONUtils.toJSON(config)+
									" servers:"+JSONUtils.toJSON(servers));
						}
						RpcFetchService.this.fireServerListeners(config, servers);
						for(RpcHostAndPort server:servers){
							List<RpcService> services = adminService.getRpcServices(server);
							if(services!=null){
								logger.info("fetchServices config:"+JSONUtils.toJSON(config)+
										" server:"+JSONUtils.toJSON(server)+
										" services:"+JSONUtils.toJSON(services));
							}
							hashMap.put(server,services);
						}

						RpcFetchService.this.fireServicesListeners(config, hashMap);
					}
				}
			});

		}
	}
	
	private void initConfig(RpcConfig config){
		logger.info("init condig:"+JSONUtils.toJSON(config));
		try{
			String protocol = config.getProtocol();
			RpcProtocol rpcProtocol = RpcProtocol.getByName(protocol);
			if(rpcProtocol==RpcProtocol.SIMPLE){
				SimpleRpcAdminService adminService = new SimpleRpcAdminService();
				adminService.setHost(config.getProviderHost());
				adminService.setPort(config.getProviderPort());
				adminService.setNamespace(config.getNamespace());
				adminService.startService();
				configAdminCache.put(config, adminService);
			}else if(rpcProtocol==RpcProtocol.ETCD){
				EtcdRpcAdminService adminService = new EtcdRpcAdminService();
				adminService.setNamespace(config.getNamespace());
				adminService.setEtcdUrl(config.getEtcdUrl());
				adminService.startService();
				configAdminCache.put(config, adminService);
			}else if(rpcProtocol==RpcProtocol.REDIS){
				RedisRpcAdminService adminService = new RedisRpcAdminService();
				adminService.setNamespace(config.getNamespace());
				adminService.setRedisHost(config.getRedisHost());
				adminService.setRedisPort(config.getRedisPort());
				adminService.setRedisMasterName(config.getSentinelMaster());
				adminService.setRedisSentinels(config.getSentinels());
				adminService.startService();
				configAdminCache.put(config, adminService);
			}else if(rpcProtocol==RpcProtocol.ZOOKEEPER){
				ZkRpcAdminService adminService = new ZkRpcAdminService();
				adminService.setNamespace(config.getNamespace());
				adminService.setConnectString(config.getZkConnectionString());
				adminService.startService();
				configAdminCache.put(config, adminService);
			}
		}catch(Exception e){
			logger.error("start admin error:"+JSONUtils.toJSON(config));
		}
	}
	
	private void startAdminService(){
		if(rpcConfiguration!=null&&rpcConfiguration.size()>0){
			for(RpcConfig config:rpcConfiguration){
				this.initConfig(config);
			}
		}else{
			throw new RpcException("configration null,please check configuration");
		}
		logger.info("started admin service");
	}
	
	private void stopAdmin(){
		Set<RpcConfig> configs = configAdminCache.keySet();
		for(RpcConfig config:configs){
			RpcAdminService adminService = configAdminCache.get(config);
			if(adminService!=null){
				adminService.stopService();
			}
		}
	}
	
	@Override
	public void stopService() {
		logger.info("stop fetch server and services");
		this.stopAdmin();
		timer.cancel();
		infoListeners.clear();
		configAdminCache.clear();
	}
	
}
