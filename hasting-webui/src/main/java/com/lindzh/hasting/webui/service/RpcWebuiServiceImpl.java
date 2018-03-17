package com.lindzh.hasting.webui.service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.lindzh.hasting.rpc.RpcService;
import com.lindzh.hasting.cluster.MD5Utils;
import com.lindzh.hasting.rpc.cluster1.RpcHostAndPort;


public class RpcWebuiServiceImpl implements RpcWebuiService,RpcInfoListener{
	
	private ConcurrentHashMap<String,RpcConfig> md5ConfigCache = new ConcurrentHashMap<String,RpcConfig>();
	
	private ConcurrentHashMap<String, Set<RpcService>> namespaceServices = new ConcurrentHashMap<String, Set<RpcService>>();
	
	private HashMap<String,Set<RpcHostAndPort>> namespaceServiceHosts = new HashMap<String,Set<RpcHostAndPort>>();
	
	private ConcurrentHashMap<String,List<RpcHostAndPort>> configProvidersCache = new ConcurrentHashMap<String,List<RpcHostAndPort>>();
	
	private ReadWriteLock readwriteLock = new ReentrantReadWriteLock(false);
	
	private HashMap<String,Set<RpcService>> namespaceHostServicesCache = new HashMap<String,Set<RpcService>>();
	
	@Override
	public void onServers(RpcConfig config, List<RpcHostAndPort> hosts) {
		Lock lock = readwriteLock.writeLock();
		try{
			lock.lock();
			String md5 = MD5Utils.md5(config.toString());
			config.setMd5(md5);
			md5ConfigCache.put(md5, config);
			configProvidersCache.put(md5, hosts);
		}finally{
			lock.unlock();
		}
	}

	@Override
	public void onServices(RpcConfig config,Map<RpcHostAndPort,List<RpcService>> hostServiceMap) {
		Lock lock = readwriteLock.writeLock();
		try{
			lock.lock();
			//namespace services
			String md5 = MD5Utils.md5(config.toString());
			Set<RpcService> set = namespaceServices.get(md5);
			if(set==null){
				namespaceServices.put(md5, new HashSet<RpcService>());
				set = namespaceServices.get(md5);
			}

			HashMap<String, Set<RpcHostAndPort>> serviceHostsMap = new HashMap<String, Set<RpcHostAndPort>>();
			HashMap<String, Set<RpcService>> hostServicesMap = new HashMap<String, Set<RpcService>>();

			Set<RpcHostAndPort> hostAndPorts = hostServiceMap.keySet();
			for(RpcHostAndPort hostAndPort: hostAndPorts){
				set.addAll(hostServiceMap.get(hostAndPort));

				List<RpcService> rpcServices = hostServiceMap.get(hostAndPort);

				String servicesKey = this.genhostServicesKey(md5, hostAndPort.getHost()+":"+hostAndPort.getPort());
				hostServicesMap.put(servicesKey,new HashSet<RpcService>(rpcServices));

				for(RpcService service :rpcServices){
					String genKey = this.genKey(md5, service.getName(), service.getVersion());
					Set<RpcHostAndPort> hosts = serviceHostsMap.get(genKey);
					if(hosts==null){
						hosts = new HashSet<RpcHostAndPort>();
						serviceHostsMap.put(genKey, hosts);
					}
					hosts.add(hostAndPort);
				}
			}

			namespaceServiceHosts = serviceHostsMap;
			namespaceHostServicesCache = hostServicesMap;
		}finally{
			lock.unlock();
		}
	}

	@Override
	public List<RpcService> search(String namespace, String keyword) {
		LinkedList<RpcService> result = new LinkedList<RpcService>();
		Lock lock = readwriteLock.readLock();
		try{
			lock.lock();
			Set<RpcService> services = namespaceServices.get(namespace);
			if(services!=null){
				for(RpcService service:services){
					if(keyword==null||keyword.length()<1){
						result.add(service);
					}else{
						String name = service.getName().toLowerCase();
						String key = keyword.toLowerCase();
						if(name.contains(key)){
							result.add(service);
						}
					}
				}
			}
		}finally{
			lock.unlock();
		}
		return result;
	}
	
	private String genhostServicesKey(String namespace,String hostAndPort){
		return MD5Utils.md5(namespace+"_"+hostAndPort);
	}

	@Override
	public List<RpcService> getServicesByHost(String namespace, String hostAndPort) {
		String servicesKey = this.genhostServicesKey(namespace, hostAndPort);
		List<RpcService> list = new ArrayList<RpcService>();
		Lock lock = readwriteLock.readLock();
		try{
			lock.lock();
			Set<RpcService> set = namespaceHostServicesCache.get(servicesKey);
			if(set!=null){
				list.addAll(set);
			}
		}finally{
			lock.unlock();
		}
		return list;
	}

	@Override
	public List<String> getNamespaces() {
		List<String> list = new ArrayList<String>();
		Lock lock = readwriteLock.readLock();
		try{
			lock.lock();
			Set<String> keys = md5ConfigCache.keySet();
			if(keys!=null){
				list.addAll(keys);
			}
		}finally{
			lock.unlock();
		}
		return list;
	}
	
	private String genKey(String namespace, String serviceName, String serviceVersion){
		return MD5Utils.md5(namespace+"_"+serviceName+"_"+serviceVersion);
	}

	@Override
	public List<RpcHostAndPort> getRpcHostsByRpc(String namespace, String serviceName, String serviceVersion) {
		ArrayList<RpcHostAndPort> hosts = new ArrayList<RpcHostAndPort>();
		String genKey = this.genKey(namespace, serviceName, serviceVersion);
		Lock lock = readwriteLock.readLock();
		try{
			lock.lock();
			Set<RpcHostAndPort> containHosts = namespaceServiceHosts.get(genKey);
			if(containHosts!=null){
				hosts.addAll(containHosts);
			}
		}finally{
			lock.unlock();
		}
		return hosts;
	}

	@Override
	public List<RpcHostAndPort> getHostsByNamespace(String namespace) {
		List<RpcHostAndPort> list = new ArrayList<RpcHostAndPort>();
		Lock lock = readwriteLock.readLock();
		try{
			lock.lock();
			List<RpcHostAndPort> hosts = configProvidersCache.get(namespace);
			if(hosts!=null){
				list.addAll(hosts);
			}
		}finally{
			lock.unlock();
		}
		return list;
	}

	@Override
	public RpcConfig getNamespaceConfig(String namespace) {
		return md5ConfigCache.get(namespace);
	}

	@Override
	public List<RpcConfig> getRpcConfigs() {
		List<RpcConfig> list = new ArrayList<RpcConfig>();
		Lock lock = readwriteLock.readLock();
		try{
			lock.lock();
			Collection<RpcConfig> configs = md5ConfigCache.values();
			if(configs!=null){
				list.addAll(configs);
			}
		}finally{
			lock.unlock();
		}
		return list;
	}
	
}
