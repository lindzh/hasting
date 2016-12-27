package com.linda.framework.rpc.server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.linda.framework.rpc.RemoteCall;
import com.linda.framework.rpc.RemoteExecutor;
import com.linda.framework.rpc.RpcServiceBean;
import com.linda.framework.rpc.exception.RpcException;
import com.linda.framework.rpc.exception.RpcExceptionHandler;
import com.linda.framework.rpc.exception.SimpleRpcExceptionHandler;
import com.linda.framework.rpc.utils.RpcUtils;
import com.linda.framework.rpc.utils.XAliasUtils;

/**
 * provider业务代码执行
 * @author lindezhi
 * 2016年6月14日 上午10:18:37
 */
public class SimpleServerRemoteExecutor implements RemoteExecutor,RpcServicesHolder{
	
	/**
	 * remote api注册
	 */
	protected ConcurrentHashMap<String,RpcServiceBean> exeCache = new ConcurrentHashMap<String,RpcServiceBean>();

	/**
	 * 业务方法执行异常处理器
	 */
	private RpcExceptionHandler exceptionHandler;

	/**
	 * 当前应用
	 */
	private String application;
	
	public SimpleServerRemoteExecutor(){
		exceptionHandler = new SimpleRpcExceptionHandler();
	}
	
	/**
	 * 使用反射调用业务方法执行
	 */
	@Override
	public void oneway(RemoteCall call) {
		RpcUtils.invokeMethod(this.findService(call), call.getMethod(), call.getArgs(),exceptionHandler);
	}

	/**
	 * 使用反射调用业务方法执行
	 */
	@Override
	public Object invoke(RemoteCall call) {
		return RpcUtils.invokeMethod(this.findService(call), call.getMethod(), call.getArgs(),exceptionHandler);
	}
	
	/**
	 * 注册remote服务
	 * @param clazz
	 * @param ifaceImpl
	 */
	public void registerRemote(Class<?> clazz,Object ifaceImpl){
		this.registerRemote(clazz, ifaceImpl,null,null);
	}
	
	/**
	 * 注册remote服务
	 * @param clazz
	 * @param ifaceImpl
	 * @param version
	 */
	public void registerRemote(Class<?> clazz,Object ifaceImpl,String version,String group){
		//validate impl java object
		Object service = exeCache.get(clazz.getName());
		if(service!=null&&service!=ifaceImpl){
			throw new RpcException("can't register service "+clazz.getName()+" again");
		}
		if(ifaceImpl==service||ifaceImpl==null){
			return;
		}
		if(version==null){
			version=RpcUtils.DEFAULT_VERSION;
		}

		//默认分组
		if(group==null){
			group = RpcUtils.DEFAULT_GROUP;
		}
		//添加类型映射
		XAliasUtils.addServiceRefType(clazz);

		exeCache.put(this.genExeKey(clazz.getName(), version,group), new RpcServiceBean(clazz,ifaceImpl,version,application,group));
	}
	
	private String genExeKey(String service,String version,String group){
		if(version!=null){
			return group+"_"+service+"_"+version;
		}
		return service;
	}
	
	/**
	 * 通过service和版本找到实现对象
	 * @param call
	 * @return
	 */
	private Object findService(RemoteCall call){
		String exeKey = this.genExeKey(call.getService(), call.getVersion(),call.getGroup());
		RpcServiceBean object = exeCache.get(exeKey);
		if(object==null||object.getBean()==null){
			throw new RpcException("group:"+call.getGroup()+" service:"+call.getService()+" version:"+call.getVersion()+" not exist");
		}
		return object.getBean();
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

	/**
	 * 获取列表，用于监控使用
	 */
	public List<RpcServiceBean> getRpcServices(){
		ArrayList<RpcServiceBean> list = new ArrayList<RpcServiceBean>();
		list.addAll(exeCache.values());
		return list;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}
}
