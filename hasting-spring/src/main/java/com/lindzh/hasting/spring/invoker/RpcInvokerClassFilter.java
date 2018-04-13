package com.lindzh.hasting.spring.invoker;

public interface RpcInvokerClassFilter {
	
	public boolean accept(Class<?> clazz);

}
