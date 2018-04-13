package com.lindzh.hasting.spring.invoker;

import java.util.HashMap;
import java.util.Map;

import com.lindzh.hasting.rpc.client.AbstractRpcClient;
import com.lindzh.hasting.spring.annotation.RpcInvokerService;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.StringUtils;


public class RpcInvokerFactoryBean<T> implements FactoryBean<T> {

	private Class<T> invokerInterface;
	private Map<String,AbstractRpcClient> rpcClientCache = new HashMap<String,AbstractRpcClient>();
	
	
	public Class<T> getInvokerInterface() {
		return invokerInterface;
	}

	public void setInvokerInterface(Class<T> invokerInterface) {
		this.invokerInterface = invokerInterface;
	}
	
	public Map<String, AbstractRpcClient> getRpcClientCache() {
		return rpcClientCache;
	}

	public void setRpcClientCache(Map<String, AbstractRpcClient> rpcClientCache) {
		this.rpcClientCache = rpcClientCache;
	}

	@Override
	public T getObject() throws Exception {
		RpcInvokerService invokerService = this.invokerInterface.getAnnotation(RpcInvokerService.class);
		String rpcServer = invokerService.rpcServer();
		if(!StringUtils.hasText(rpcServer)){
			rpcServer = "defaultRpcClient";
		}
		AbstractRpcClient rpcClient = rpcClientCache.get(rpcServer);
		if(rpcClient==null){
			throw new BeanCreationException("can't find rpc client of name:"+rpcServer);
		}
		return rpcClient.register(invokerInterface,invokerService.version());
	}

	@Override
	public Class<?> getObjectType() {
		return invokerInterface;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
}
