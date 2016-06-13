package com.linda.framework.rpc.generic;

/**
 * 泛型
 * @author linda
 * 
 *
 */
public interface GenericService {

	/**
	 * 需要等待返回值，或者同步的调用
	 * @param service
	 * @param version
	 * @param method
	 * @param argtype
	 * @param args
	 * @return
	 */
	public Object invoke(String service,String version,String method,String[] argtype,Object[] args);
	
	/**
	 * 不需要等待返回值的调用
	 * @param service
	 * @param version
	 * @param method
	 * @param argtype
	 * @param args
	 */
	public void oneway(String service,String version,String method,String[] argtype,Object[] args);

}
