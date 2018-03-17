package com.lindzh.hasting.rpc;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * rpc请求线程同步bean
 * @author lindezhi
 * 2016年3月9日 上午11:25:59
 */
public class RpcCallSync implements Future{
	
	private int index;
	
	/**
	 * 请求发送包
	 */
	private RpcObject request;
	
	/**
	 * 请求返回数据包
	 */
	private RpcObject response;

	public RpcCallSync(int index,RpcObject request){
		this.index = index;
		this.request = request;
	}
	
	public RpcObject getRequest() {
		return request;
	}

	public void setRequest(RpcObject request) {
		this.request = request;
	}

	public RpcObject getResponse() {
		return response;
	}

	public void setResponse(RpcObject response) {
		this.response = response;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return false;
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public boolean isDone() {
		return response!=null;
	}

	@Override
	public Object get() throws InterruptedException, ExecutionException {
		return response;
	}

	@Override
	public Object get(long timeout, TimeUnit unit) throws InterruptedException,
			ExecutionException, TimeoutException {
		return null;
	}
	
}
