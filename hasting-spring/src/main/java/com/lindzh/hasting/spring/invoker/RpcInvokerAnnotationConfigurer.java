package com.lindzh.hasting.spring.invoker;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.lindzh.hasting.rpc.client.AbstractRpcClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


public class RpcInvokerAnnotationConfigurer implements
		BeanDefinitionRegistryPostProcessor,ApplicationContextAware {

	private List<String> packages;

	private Map<String, AbstractRpcClient> rpcClients;
	
	private ApplicationContext apc;

	public List<String> getPackages() {
		return packages;
	}

	public void setPackages(List<String> packages) {
		this.packages = packages;
	}

	public Map<String, AbstractRpcClient> getRpcClients() {
		return rpcClients;
	}

	public void setRpcClients(Map<String, AbstractRpcClient> rpcClients) {
		this.rpcClients = rpcClients;
	}

	@Override
	public void postProcessBeanFactory(
			ConfigurableListableBeanFactory beanFactory) throws BeansException {

	}

	@Override
	public void postProcessBeanDefinitionRegistry(
			BeanDefinitionRegistry registry) throws BeansException {
		RpcInvokerAnnotationScanner scanner = new RpcInvokerAnnotationScanner(registry, rpcClients);
		scanner.scan(packages.toArray(new String[0]));
	}

	private void startRpcs(){
		Collection<AbstractRpcClient> clients = rpcClients.values();
		for (AbstractRpcClient client : clients) {
			client.startService();
		}
	}
	
	public void stopRpcService() {
		Collection<AbstractRpcClient> clients = rpcClients.values();
		for (AbstractRpcClient client : clients) {
			client.stopService();
		}
	}

	public void setApplicationContext(ApplicationContext apc)
			throws BeansException {
		this.apc = apc;
		this.rpcClients = this.apc.getBeansOfType(AbstractRpcClient.class);
		this.startRpcs();
	}

}
