package com.lindzh.hasting.spring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 客户端rpc bean依赖注入
 * @author linda
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcInvokerService {
	
	String rpcServer() default "defaultRpcServer";
	
	String name() default "";
	
	String version() default "0.0";
}
