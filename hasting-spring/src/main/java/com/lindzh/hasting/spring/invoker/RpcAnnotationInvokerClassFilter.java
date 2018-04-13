package com.lindzh.hasting.spring.invoker;

import com.lindzh.hasting.spring.annotation.RpcInvokerService;

public class RpcAnnotationInvokerClassFilter implements RpcInvokerClassFilter{

	@Override
	public boolean accept(Class<?> clazz) {
		if(clazz.getAnnotation(RpcInvokerService.class)!=null){
			return true;
		}
		return false;
	}

}
