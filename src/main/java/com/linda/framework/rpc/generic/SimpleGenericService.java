package com.linda.framework.rpc.generic;

import com.linda.framework.rpc.RemoteCall;
import com.linda.framework.rpc.RemoteExecutor;

/**
 * 泛型支持，客户端调用泛型无需实现class
 * @author Administrator
 *
 */
public class SimpleGenericService implements GenericService{
	
	private RemoteExecutor executor;
	
	private ArgsParser argsParser;
	
	public SimpleGenericService(RemoteExecutor executor){
		this.executor = executor;
		this.argsParser = new SimpleArgsParser();
	}
	
	private Object execute(String service, String version, String method,String[] argtype,
			Object[] args){
		argsParser.checkArgs(argtype, args);
		Object[] args2 = argsParser.parseArgs(argtype, args);
		RemoteCall call = new RemoteCall(service, method);
		call.setVersion(version);
		call.setArgs(args2);
		return executor.invoke(call);
	}
	
	@Override
	public Object invoke(String service, String version, String method,String[] argtype,
			Object[] args) {
		Object result = this.execute(service, version, method, argtype, args);
		return argsParser.parseResult(result);
	}

	@Override
	public void oneway(String service, String version, String method,
			String[] argtype, Object[] args) {
		this.execute(service, version, method, argtype, args);
	}
}
