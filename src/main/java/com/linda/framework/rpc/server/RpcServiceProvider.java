package com.linda.framework.rpc.server;

import org.apache.log4j.Logger;

import com.linda.framework.rpc.RemoteCall;
import com.linda.framework.rpc.RemoteExecutor;
import com.linda.framework.rpc.RpcContext;
import com.linda.framework.rpc.RpcObject;
import com.linda.framework.rpc.Service;
import com.linda.framework.rpc.exception.RpcExceptionHandler;
import com.linda.framework.rpc.exception.SimpleRpcExceptionHandler;
import com.linda.framework.rpc.filter.RpcFilter;
import com.linda.framework.rpc.filter.RpcFilterChain;
import com.linda.framework.rpc.filter.SimpleRpcFilterChain;
import com.linda.framework.rpc.net.RpcCallListener;
import com.linda.framework.rpc.net.RpcSender;
import com.linda.framework.rpc.serializer.JdkSerializer;
import com.linda.framework.rpc.serializer.RpcSerializer;
import com.linda.framework.rpc.utils.RpcUtils;
import com.linda.framework.rpc.utils.RpcUtils.RpcType;

/**
 * 接收rpcObject，业务参数反序列化，执行filter，调用最终的业务executor调用业务代码
 * @author lindezhi
 * 2015年6月14日 上午9:41:45
 */
public class RpcServiceProvider implements RpcCallListener,RpcFilter,Service{
	
	private Logger logger = Logger.getLogger(RpcServiceProvider.class);
	
	/**
	 * 提交给上层的执行器
	 */
	private RemoteExecutor executor;
	
	private RpcSerializer serializer;
	
	/**
	 * 发送返回值超时时间
	 */
	private int timeout = 200;
	
	private RpcExceptionHandler exceptionHandler;
	
	private RpcFilterChain filterChain;
	
	public RpcServiceProvider(){
		serializer = new JdkSerializer();
		exceptionHandler = new SimpleRpcExceptionHandler();
		filterChain = new SimpleRpcFilterChain();
	}
	
	/**
	 * tcp接收到rpcobject后调用该方法
	 */
	@Override
	public void onRpcMessage(RpcObject rpc, RpcSender sender) {
		RemoteCall call = this.deserializeCall(rpc, sender);
		//服务提供方得到上下文
		RpcContext.getContext().putAll(call.getAttachment());
		try{
			if(call!=null){
				filterChain.startFilter(rpc, call, sender);
			}
		}catch(Exception e){
			//出现异常直接返回异常
			this.handleException(rpc, call, sender, e);
		}
		
	}
	
	/**
	 * 反序列化执行
	 * @param rpc
	 * @param sender
	 * @return
	 */
	private RemoteCall deserializeCall(RpcObject rpc, RpcSender sender){
		try{
			return (RemoteCall)serializer.deserialize(rpc.getData());
		}catch(Exception e){
			//出现异常直接返回异常给调用方
			this.handleException(rpc, null, sender, e);
			return null;
		}
	}
	
	/**
	 * 同步调用执行
	 * @param call rpc上下文数据
	 * @param threadId 调用方线程id
	 * @param index 调用方seq
	 * @param sender 用于发送执行结果
	 */
	private void execute(RemoteCall call,long threadId,int index,RpcSender sender){
		RpcObject rpc = this.createRpcObject(index);
		rpc.setThreadId(threadId);
		//执行提交给上层应用
		Object result = executor.invoke(call);
		//生成结果
		rpc.setType(RpcType.SUC);
		if (result != null) {
			byte[] data = serializer.serialize(result);
			rpc.setLength(data.length);
			rpc.setData(data);
		}else{
			rpc.setLength(0);
			rpc.setData(new byte[0]);
		}
		//结果返回
		sender.sendRpcObject(rpc, timeout);
	}
	
	private RpcObject createRpcObject(int index){
		return new RpcObject(0,index, 0, null);
	}
	
	public RemoteExecutor getExecutor() {
		return executor;
	}

	public void setExecutor(RemoteExecutor executor) {
		this.executor = executor;
	}

	public RpcExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

	public void setExceptionHandler(RpcExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

	public RpcSerializer getSerializer() {
		return serializer;
	}

	public void setSerializer(RpcSerializer serializer) {
		this.serializer = serializer;
	}

	public void addRpcFilter(RpcFilter filter){
		filterChain.addRpcFilter(filter);
	}

	public void setFilterChain(RpcFilterChain filterChain) {
		this.filterChain = filterChain;
	}

	@Override
	public void startService() {
		filterChain.addRpcFilter(this);
	}

	@Override
	public void stopService() {
		
	}

	private void handleException(RpcObject rpc, RemoteCall call, RpcSender sender,Exception e){
		RpcUtils.handleException(exceptionHandler,rpc,call,e);
		if(rpc.getType()==RpcType.INVOKE){
			//生成异常数据
			RpcObject respRpc = this.createRpcObject(rpc.getIndex());
			respRpc.setThreadId(rpc.getThreadId());
			respRpc.setType(RpcType.FAIL);
			String message = e.getMessage();
			if(message!=null){
				byte[] data = message.getBytes();
				respRpc.setLength(data.length);
				if(data.length>0){
					respRpc.setData(data);
				}
			}
			//调用失败异常返回
			sender.sendRpcObject(respRpc, timeout);
		}
	}
	
	/**
	 * 业务调用处理，业务处理是最后一个filter
	 */
	@Override
	public void doFilter(RpcObject rpc, RemoteCall call, RpcSender sender,
			RpcFilterChain chain) {
		int index = rpc.getIndex();
		if (rpc.getType() == RpcType.ONEWAY) {
			executor.oneway(call);
		} else if (rpc.getType() == RpcType.INVOKE) {
			this.execute(call, rpc.getThreadId(), index, sender);
		}
	}
}
