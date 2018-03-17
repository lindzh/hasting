package com.lindzh.hasting.cluster;

public class RpcMessage<T> {
	
	private int messageType;
	
	private T message;
	
	public RpcMessage(){
		
	}
	
	public RpcMessage(int messageType,T message){
		this.messageType = messageType;
		this.message = message;
	}

	public int getMessageType() {
		return messageType;
	}

	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}

	public T getMessage() {
		return message;
	}

	public void setMessage(T message) {
		this.message = message;
	}
}
