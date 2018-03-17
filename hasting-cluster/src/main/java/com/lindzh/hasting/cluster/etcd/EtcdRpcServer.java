package com.lindzh.hasting.cluster.etcd;

import java.util.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.lindzh.hasting.rpc.cluster1.RpcClusterServer;
import com.lindzh.hasting.rpc.cluster1.RpcHostAndPort;
import com.lindzh.hasting.cluster.limit.LimitCache;
import com.lindzh.hasting.cluster.limit.LimitFilter;
import com.lindzh.hasting.cluster.limit.LimitDefine;
import com.linda.jetcd.EtcdChangeResult;
import com.linda.jetcd.EtcdResult;
import com.linda.jetcd.EtcdWatchCallback;
import com.lindzh.hasting.cluster.JSONUtils;
import org.apache.log4j.Logger;

import com.lindzh.hasting.rpc.RpcService;
import com.lindzh.hasting.cluster.MD5Utils;
import com.lindzh.hasting.rpc.net.RpcNetBase;
import com.linda.jetcd.EtcdClient;

/**
 * 
 * @author lindezhi 用 coreos的etcd实现server列表与rpc服务列表动态通知与管理
 */
public class EtcdRpcServer extends RpcClusterServer {

	private EtcdClient etcdClient;

	private String etcdUrl;

	private String namespace = "rpc";

	private List<RpcService> rpcServiceCache = new ArrayList<RpcService>();

	private RpcNetBase network;

	private Timer timer = new Timer();

	private long notifyTtl = 30;// 默认5分钟

	private int serverttl = 60;// 1min

	private String serverMd5 = null;
	
	private long time = 0;

	private LimitCache limitCache = new LimitCache();
	
	private Logger logger = Logger.getLogger("rpcCluster");

	private String genServerKey() {
		return "/"+namespace+"/servers/" + this.serverMd5;
	}

	private String genServerServiceKey() {
		return "/"+namespace+"/services/" + this.serverMd5;
	}



	private Random random = new Random();

	private String genServiceKey(String serviceMd5) {
		return "/"+namespace+"/services/" + this.serverMd5 + "/" + serviceMd5;
	}

	public EtcdRpcServer() {

	}

	@Override
	public void onClose(RpcNetBase network, Exception e) {
		this.cleanIfExist();
		this.stopHeartBeat();
		etcdClient.stop();
	}

	@Override
	public void startService() {
		this.addRpcFilter(new LimitFilter(limitCache));
		logger.info("[SERVER] limit filter added");
		super.startService();
	}

	/**
	 * 启动成功之后,添加service,然后添加server
	 * @param network
     */
	@Override
	public void onStart(RpcNetBase network) {
		time = System.currentTimeMillis();
		etcdClient = new EtcdClient(etcdUrl);
		etcdClient.start();
		this.serverMd5 = MD5Utils.hostMd5(this.getApplication(),network.getHost(),network.getPort());
		this.network = network;
		this.checkAndAddRpcService();
		this.startHeartBeat();

		this.doSetWehgit(getApplication(),this.getHost()+":"+this.getPort(),100,false);
		//限流获取与监控
		fetchLimit();
		watchLimit();
	}

	private void stopHeartBeat() {
		timer.cancel();
		timer = null;
	}

	private void startHeartBeat() {
		Date start = new Date(System.currentTimeMillis() + 1000L);
		timer.scheduleAtFixedRate(new HeartBeatTask(), start, notifyTtl*1000);
	}

	/**
	 * 添加服务器ip地址到注册中心
	 */
	private void cleanIfExist() {
		// 删除server
		String serverKey = this.genServerKey();
		//null
		this.etcdClient.del(serverKey);
		// 删除server的service列表
		String serverServiceKey = this.genServerServiceKey();
		this.etcdClient.delDir(serverServiceKey, true);
	}

	/**
	 * 添加服务列表到注册中心
	 */
	private void checkAndAddRpcService() {
		this.cleanIfExist();
		for (RpcService rpcService : rpcServiceCache) {
			this.addRpcService(rpcService);
		}
		this.updateServerTtl();
	}

	private void addRpcService(RpcService service) {
		String serviceMd5 = MD5Utils.serviceMd5(service);
		String serviceKey = this.genServiceKey(serviceMd5);
		String serviceJson = JSONUtils.toJSON(service);
		logger.info("addRpcService:"+serviceJson);
		this.etcdClient.set(serviceKey, serviceJson);
	}

	@Override
	protected void doRegister(Class<?> clazz, Object ifaceImpl, String version,String group) {
		RpcService service = new RpcService(clazz.getName(), version, ifaceImpl.getClass().getName());

		//增加application
		service.setApplication(this.getApplication());
		//增加分组支持
		service.setGroup(group);

		service.setTime(System.currentTimeMillis());
		if (this.network != null) {
			this.rpcServiceCache.add(service);
			this.addRpcService(service);
		} else {
			this.rpcServiceCache.add(service);
		}
	}

	/**
	 * 添加服务器到注册中心,不断更新
	 */
	private void updateServerTtl() {
		RpcHostAndPort hostAndPort = new RpcHostAndPort(network.getHost(),network.getPort());

		hostAndPort.setTime(time);
		//token
		hostAndPort.setToken(this.getToken());

		hostAndPort.setApplication(this.getApplication());

		String serverKey = this.genServerKey();
		String hostAndPortJson = JSONUtils.toJSON(hostAndPort);
		logger.info("updateServerTTL:"+hostAndPortJson);
		this.etcdClient.set(serverKey, hostAndPortJson, serverttl);
	}

	private class HeartBeatTask extends TimerTask {
		@Override
		public void run() {
			EtcdRpcServer.this.updateServerTtl();
		}
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getEtcdUrl() {
		return etcdUrl;
	}

	public void setEtcdUrl(String etcdUrl) {
		this.etcdUrl = etcdUrl;
	}

	private void doSetWehgit(String application,String key,int weight,boolean override){
		String hostWeightKey = this.genApplicationWeightHostkey(application,key);
		String value = ""+weight;
		if(override){
			this.etcdClient.set(hostWeightKey,value);
		}else{
			EtcdResult result = this.etcdClient.get(hostWeightKey);
			if(result.isSuccess()){
				return;
			}
			//是否存在
			this.etcdClient.set(hostWeightKey,value);
		}
		//notify change
		String weightWatchKey = this.genApplicationWeightWatchKey(application);
		this.etcdClient.set(weightWatchKey,"weight_"+random.nextInt(10000000));
	}

	private String genApplicationWeightWatchKey(String application){
		return "/"+namespace+"/weight/"+application+"/node";
	}

	private String genApplicationWeightHostkey(String application,String hostkey){
		return "/"+namespace+"/weight/"+application+"/weights/"+hostkey;
	}

	/**
	 * 获取限流列表
	 */
	private void fetchLimit(){
		EtcdResult etcdResult = this.etcdClient.get(EtcdUtils.genLimitKey(namespace,this.getApplication()));
		if(etcdResult.isSuccess()){
			String value = etcdResult.getNode().getValue();
			if(value!=null){
				List<LimitDefine> limits = JSONUtils.fromJSON(value, new TypeReference<List<LimitDefine>>() {});
				logger.info("[ETCD] limit has fetched data is:"+value);
				limitCache.addOrUpdate(limits);
			}else{
				logger.info("[ETCD] limit has fetched empty data");
			}
		}else{
			logger.info("[ETCD] limit get limit failed");
		}
	}

	/**
	 * 监控限流配置
	 */
	private void watchLimit(){
		this.etcdClient.watch(EtcdUtils.genLimitKey(namespace,this.getApplication()), new EtcdWatchCallback() {
			@Override
			public void onChange(EtcdChangeResult future) {
				if(future.isOk()){
					if(future.getResult()!=null&&future.getResult().getNode()!=null){
						String value = future.getResult().getNode().getValue();
						if(value!=null){
							List<LimitDefine> limits = JSONUtils.fromJSON(value, new TypeReference<List<LimitDefine>>() {});
							logger.info("[ETCD] event limit has fetched data is:"+value);
							limitCache.addOrUpdate(limits);
						}
					}else{
						logger.error("[ETCD] limit event repsonse node not exist");
					}
				}else{
					logger.error("[ETCD] limit watch event failed,message is:"+future.getFailReason());
				}
				watchLimit();
			}
		});
	}

}
