package com.lindzh.hasting.spring.invoker;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.lindzh.hasting.rpc.client.AbstractRpcClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;


public class RpcInvokerAnnotationScanner extends ClassPathBeanDefinitionScanner{
	
	static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";
	
	private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

	private MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(this.resourcePatternResolver);

	private String resourcePattern = DEFAULT_RESOURCE_PATTERN;

	public RpcInvokerAnnotationScanner(BeanDefinitionRegistry registry,Map<String,AbstractRpcClient> rpcClients) {
		super(registry,false);
		this.rpcClients = rpcClients;
		super.addIncludeFilter(new TypeFilter(){
			@Override
			public boolean match(MetadataReader metadataReader,MetadataReaderFactory metadataReaderFactory)throws IOException {
				String className = metadataReader.getClassMetadata().getClassName();
				return acceptClassName(className);
			}
		});
	}
	
	private boolean acceptClassName(String className){
		ClassLoader loader = RpcInvokerAnnotationScanner.class.getClassLoader();
		try {
			Class<?> clazz = loader.loadClass(className);
			return classFilter.accept(clazz);
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	private Logger logger = Logger.getLogger(RpcInvokerAnnotationScanner.class);
	
	private Map<String,AbstractRpcClient> rpcClients;
	
	public Map<String, AbstractRpcClient> getRpcClients() {
		return rpcClients;
	}

	public void setRpcClients(Map<String, AbstractRpcClient> rpcClients) {
		this.rpcClients = rpcClients;
	}

	private RpcInvokerClassFilter classFilter = new RpcAnnotationInvokerClassFilter();
	

	@Override
	protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
		HashSet<BeanDefinitionHolder> beans = new HashSet<BeanDefinitionHolder>();
		Set<BeanDefinitionHolder> set = super.doScan(basePackages);
		for(BeanDefinitionHolder bdh:set){
			GenericBeanDefinition bdf = (GenericBeanDefinition)bdh.getBeanDefinition();
			bdf.getPropertyValues().add("invokerInterface", bdf.getBeanClassName());
			bdf.getPropertyValues().add("rpcClientCache", rpcClients);
			bdf.setBeanClass(RpcInvokerFactoryBean.class);
		}
		return beans;
	}

	@Override
	protected boolean isCandidateComponent(
			AnnotatedBeanDefinition beanDefinition) {
		String beanClassName = beanDefinition.getBeanClassName();
		return this.acceptClassName(beanClassName);
	}
	
	
}
