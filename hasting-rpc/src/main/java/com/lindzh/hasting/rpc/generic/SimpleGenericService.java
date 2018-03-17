package com.lindzh.hasting.rpc.generic;

import com.lindzh.hasting.rpc.RemoteCall;
import com.lindzh.hasting.rpc.RemoteExecutor;

/**
 * 泛型支持，客户端调用泛型无需实现class
 * @author Administrator
 * 泛化调用，方式是consumer用java的map替换对象，同时把类型传过来，provider做类型转换，
 * 然后调用相关服务，同时把返回值封装为java自带类型（对象封装为map）
 */
public class SimpleGenericService implements GenericService{
	
	/**
	 * 上层服务执行器
	 */
	private RemoteExecutor executor;
	
	/**
	 * 参数转换器
	 */
	private ArgsParser argsParser;
	
	public SimpleGenericService(RemoteExecutor executor){
		this.executor = executor;
		this.argsParser = new SimpleArgsParser();
	}
	
	/**
	 * 转换参数并调用上层服务执行
	 * @param service
	 * @param version
	 * @param method
	 * @param argtype
	 * @param args
	 * @return
	 */
	private Object execute(String group,String service, String version, String method,String[] argtype,
			Object[] args){
		argsParser.checkArgs(argtype, args);
		Object[] args2 = argsParser.parseArgs(argtype, args);
		RemoteCall call = new RemoteCall(service, method);
		call.setVersion(version);
		call.setArgs(args2);
		call.setGroup(group);
		return executor.invoke(call);
	}
	
	/**
	 * 同步调用
	 */
	@Override
	public Object invoke(String group,String service, String version, String method,String[] argtype,
			Object[] args) {
		//执行
		Object result = this.execute(group,service, version, method, argtype, args);
		//invoke需要返回值，需要转换执行结果
		return argsParser.parseResult(result);
	}

	/**
	 * 异步调用
	 */
	@Override
	public void oneway(String group,String service, String version, String method,
			String[] argtype, Object[] args) {
		//转换入参，执行
		this.execute(group,service, version, method, argtype, args);
		//无需返回值
	}
}
