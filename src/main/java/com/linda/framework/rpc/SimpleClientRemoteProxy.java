package com.linda.framework.rpc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class SimpleClientRemoteProxy implements InvocationHandler,Service{

	private RemoteExecutor remoteExecutor;
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Class<?> service = method.getDeclaringClass();
		String name = method.getName();
		RemoteCall call = new RemoteCall(service.getName(),name);
		call.setArgs(args);
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
		return (Iface)Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{remote}, this);
	}

	@Override
	public void startService() {
		remoteExecutor.startService();
	}

	@Override
	public void stopService() {
		remoteExecutor.stopService();
	}
}
