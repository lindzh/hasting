package com.linda.framework.rpc.net;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.linda.framework.rpc.RpcObject;
import com.linda.framework.rpc.Service;
import com.linda.framework.rpc.exception.RpcNetExceptionHandler;

public abstract class RpcNetBase extends AbstractRpcNetworkBase implements RpcNetExceptionHandler{
	
	//时间分配执行器
	private ExecutorService executorService;
	//sharable connector可以关闭，否则不能
	private boolean executorSharable;

	protected List<RpcCallListener> callListeners;
	
	private static final int DEFAULT_EXECUTOR_THREAD_COUNT = 3;
	//执行器数量
	private int executorThreadCount = DEFAULT_EXECUTOR_THREAD_COUNT;
	
	public RpcNetBase(){
		callListeners = new LinkedList<RpcCallListener>();
	}
	
	public ExecutorService getExecutorService() {
		return executorService;
	}

	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}

	public boolean isExecutorSharable() {
		return executorSharable;
	}

	public void setExecutorSharable(boolean executorSharable) {
		this.executorSharable = executorSharable;
	}

	public int getExecutorThreadCount() {
		return executorThreadCount;
	}

	public void setExecutorThreadCount(int executorThreadCount) {
		this.executorThreadCount = executorThreadCount;
	}
	
	public void addRpcCallListener(RpcCallListener listener){
		callListeners.add(listener);
	}
	
	public List<RpcCallListener> getCallListeners() {
		return callListeners;
	}

	public void fireCallListeners(RpcObject rpc,RpcSender sender){
		for(RpcCallListener listener:callListeners){
			listener.onRpcMessage(rpc,sender);
		}
	}
	
	public void startListeners(){
		for(RpcCallListener listener:callListeners){
			if(listener instanceof Service){
				Service service = (Service)listener;
				service.startService();
			}
		}
	}
	
	public void stopListeners(){
		for(RpcCallListener listener:callListeners){
			if(listener instanceof Service){
				Service service = (Service)listener;
				service.stopService();
			}
		}
	}
	
	public void addConnectorListeners(AbstractRpcConnector connector){
		for(RpcCallListener listener:callListeners){
			connector.addRpcCallListener(listener);
		}
	}

	@Override
	public void startService() {
		if(this.executorService==null){
			if(this.executorThreadCount<1){
				this.executorThreadCount = DEFAULT_EXECUTOR_THREAD_COUNT;
			}
			executorService = Executors.newFixedThreadPool(executorThreadCount);
		}
	}

	@Override
	public void stopService() {
		if(!this.isExecutorSharable()&&executorService!=null){
			executorService.shutdown();
		}
	}
}
