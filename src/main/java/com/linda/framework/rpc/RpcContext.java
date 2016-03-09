package com.linda.framework.rpc;

import java.util.HashMap;
import java.util.Map;

/**
 * rpc上下文，用于传递附件，获取当前环境值
 * @author lindezhi
 * 2016年3月9日 上午11:25:26
 */
public class RpcContext {

	private static ThreadLocal<RpcContext> context = new ThreadLocal<RpcContext>(){
		@Override
		protected RpcContext initialValue() {
			return new RpcContext();
		}
	};
	
	public static RpcContext getContext(){
		return context.get();
	}
	
	public void clear(){
		context.remove();
	}
	
	private Map<String,Object> attachment = new HashMap<String,Object>();

	public void putAll(Map<String,Object> attachment){
		if(attachment!=null){
			this.attachment.putAll(attachment);
		}
	}
	
	public Map<String,Object> getAttachment(){
		return this.attachment;
	}
	
	public int size(){
		return this.attachment.size();
	}
	
	public Object getAttachment(String key){
		return attachment.get(key);
	}
	
	public void setAttachment(String key,Object value){
		this.attachment.put(key, value);
	}
}
