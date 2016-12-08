package com.linda.framework.rpc.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.linda.framework.rpc.RemoteCall;
import com.linda.framework.rpc.RemoteExecutor;
import com.linda.framework.rpc.RpcContext;
import com.linda.framework.rpc.Service;
import com.linda.framework.rpc.utils.RpcUtils;

public class SimpleClientRemoteProxy implements InvocationHandler,Service{

	private RemoteExecutor remoteExecutor;
	
	private ConcurrentHashMap<Class,String> versionCache = new ConcurrentHashMap<Class,String>();

	/**
	 * 应用
	 */
	private String application;
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Class<?> service = method.getDeclaringClass();
		
		String name = method.getName();
		RemoteCall call = new RemoteCall(service.getName(),name);
		call.setArgs(args);
		String version = versionCache.get(service);
		if(version!=null){
			call.setVersion(version);
		}else{
			call.setVersion(RpcUtils.DEFAULT_VERSION);
		}
		
		//加入上下文附件传送支持
		Map<String, Object> attachment = RpcContext.getContext().getAttachment();
		call.setAttachment(attachment);

		//客户点应用加入附件中
		call.getAttachment().put("Application",application);

		if(method.getReturnType()==void.class){
			remoteExecutor.oneway(call);
			return null;
		}
		return remoteExecutor.invoke(call);
	}

	public RemoteExecutor getRemoteExecutor() {
		return remoteExecutor;
	}

	public void setRemoteExecutor(RemoteExecutor remoteExecutor) {
		this.remoteExecutor = remoteExecutor;
	}
	
	public <Iface> Iface registerRemote(Class<Iface> remote){
		return this.registerRemote(remote, null);
	}
	
	public <Iface> Iface registerRemote(Class<Iface> remote,String version){
		Iface result = (Iface)Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{remote}, this);
		if(version==null){
			version = RpcUtils.DEFAULT_VERSION;
		}
		versionCache.put(remote, version);
		return result;
	}

	@Override
	public void startService() {
		remoteExecutor.startService();
	}

	@Override
	public void stopService() {
		remoteExecutor.stopService();
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}
}
