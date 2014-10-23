package com.linda.framework.rpc;

import java.util.concurrent.ConcurrentHashMap;

public class SimpleServerRemoteExecutor implements RemoteExecutor{
	
	protected ConcurrentHashMap<String,Object> exeCache = new ConcurrentHashMap<String,Object>();

	private RpcExceptionHandler exceptionHandler;
	
	public SimpleServerRemoteExecutor(){
		exceptionHandler = new SimpleRpcExceptionHandler();
	}
	
	@Override
	public void oneway(RemoteCall call) {
		RpcUtils.invokeMethod(this.findService(call), call.getMethod(), call.getArgs(),exceptionHandler);
	}

	@Override
	public Object invoke(RemoteCall call) {
		return RpcUtils.invokeMethod(this.findService(call), call.getMethod(), call.getArgs(),exceptionHandler);
	}
	
	public void registerRemote(Class clazz,Object object){
		Object service = exeCache.get(clazz.getName());
		if(service!=null&&service!=object){
			throw new RpcException("can't register service "+clazz.getName()+" again");
		}
		if(object==service||object==null){
			return;
		}
		exeCache.put(clazz.getName(), object);
	}
	
	private Object findService(RemoteCall call){
		Object object = exeCache.get(call.getService());
		if(object==null){
			throw new RpcException("service "+call.getService()+" not exist");
		}
		return object;
	}

	@Override
	public void startService() {
		
	}

	@Override
	public void stopService() {
		
	}

	public RpcExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

	public void setExceptionHandler(RpcExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}
}
