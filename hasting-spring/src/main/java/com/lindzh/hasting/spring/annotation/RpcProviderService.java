package com.lindzh.hasting.spring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 服务端提供端bean依赖注入
 * @author linda
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcProviderService {
	
	String rpcServer() default "defaultRpcServer";
	
	String version() default "0.0";
	
}
