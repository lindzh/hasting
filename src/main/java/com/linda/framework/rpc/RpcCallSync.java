package com.linda.framework.rpc;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RpcCallSync implements Future{
	
	private int index;
	
	private RpcObject request;
	
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
