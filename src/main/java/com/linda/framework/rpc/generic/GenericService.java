package com.linda.framework.rpc.generic;

/**
 * 泛型
 * @author linda
 *
 */
public interface GenericService {

	public Object invoke(String service,String version,String method,String[] argtype,Object[] args);
	
	public void oneway(String service,String version,String method,String[] argtype,Object[] args);

}
