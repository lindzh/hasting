package com.lindzh.hasting.spring.provider;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.lindzh.hasting.rpc.filter.RpcFilter;
import com.lindzh.hasting.rpc.server.AbstractRpcServer;
import com.lindzh.hasting.spring.annotation.RpcProviderFilter;
import com.lindzh.hasting.spring.annotation.RpcProviderService;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;


public class RpcProviderProcessor implements ApplicationContextAware,InitializingBean{

	private static Logger logger = 	Logger.getLogger(RpcProviderProcessor.class);
	
	private ConcurrentHashMap<String, AbstractRpcServer> serverMap = new ConcurrentHashMap<String,AbstractRpcServer>();
	private static final String DEFAULT_RPC_BEAN = "defaultRpcServer";
	
	private ApplicationContext apc;
	
	@Override
	public void setApplicationContext(ApplicationContext apc)
			throws BeansException {
		this.apc = apc;
		this.initRpcServer(apc);
		this.initRpcFilter(apc);
		this.registerRpcs(apc);
		this.startRpcServers();
	}

	private void initRpcServer(ApplicationContext apc){
		Map<String, AbstractRpcServer> servers = apc.getBeansOfType(AbstractRpcServer.class);
		Set<String> keys = servers.keySet();
		for(String key:keys){
			if(!StringUtils.hasText(key)){
				key = DEFAULT_RPC_BEAN;
			}
			AbstractRpcServer server = servers.get(key);
			serverMap.put(key, server);
			logger.info("find rpc service:"+key+" host:"+server.getHost()+":"+server.getPort());
		}
	}
	
	private void registerRpcs(ApplicationContext apc){
		Map<String, Object> map = apc.getBeansWithAnnotation(RpcProviderService.class);
		Collection<Object> values = map.values();
		for(Object obj:values){
			RpcProviderService providerService = obj.getClass().getAnnotation(RpcProviderService.class);
			Class<?>[] ifs = obj.getClass().getInterfaces();
			for(Class<?> iface:ifs){
				RpcProviderService service = providerService;
				if(service==null){
					service = iface.getAnnotation(RpcProviderService.class);
				}
				if(service!=null){
					String bean = service.rpcServer();
					if(bean==null){
						bean = DEFAULT_RPC_BEAN;
					}
					AbstractRpcServer server = serverMap.get(bean);
					if(server==null){
						throw new BeanCreationException("can't find rpcServer of name:"+bean);
					}
					server.register(iface, obj,service.version());
					logger.info("register rpc bean:"+iface+" bean:"+bean);
				}
			}
		}
		logger.info("register rpc service success");
	}
	
	private void initRpcFilter(ApplicationContext apc){
		Map<String, RpcFilter> filterMap = apc.getBeansOfType(RpcFilter.class);
		Collection<RpcFilter> filters = filterMap.values();
		for(RpcFilter filter:filters){
			RpcProviderFilter providerFilter = filter.getClass().getAnnotation(RpcProviderFilter.class);
			if(providerFilter!=null){
				String bean = providerFilter.rpcServer();
				AbstractRpcServer server = serverMap.get(bean);
				if(server==null){
					throw new BeanCreationException("inject rpcfilter can't find rpcServer of name:"+bean);
				}
				server.addRpcFilter(filter);
				logger.info("addRpcFilter "+filter+" for rpcServer "+bean);
			}else{
				Collection<AbstractRpcServer> servers = serverMap.values();
				for(AbstractRpcServer server:servers){
					logger.info("addRpcFilter "+filter+" for rpcServer "+server.getHost()+":"+server.getPort());
					server.addRpcFilter(filter);
				}
			}
		}
	}
	
	private void startRpcServers(){
		Collection<AbstractRpcServer> servers = serverMap.values();
		for(AbstractRpcServer server:servers){
			server.startService();
			logger.info("start rpc service:"+server.getHost()+":"+server.getPort());
		}
	}
	
	public void stopRpcService(){
		Collection<AbstractRpcServer> servers = serverMap.values();
		for(AbstractRpcServer server:servers){
			server.stopService();
			logger.info("stop rpc service:"+server.getHost()+":"+server.getPort());
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {

	}
}
