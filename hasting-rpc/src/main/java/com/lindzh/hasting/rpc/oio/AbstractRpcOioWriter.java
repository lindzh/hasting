package com.lindzh.hasting.rpc.oio;

import java.io.DataOutputStream;

import com.lindzh.hasting.rpc.RpcObject;
import com.lindzh.hasting.rpc.net.AbstractRpcConnector;
import com.lindzh.hasting.rpc.net.AbstractRpcWriter;
import com.lindzh.hasting.rpc.utils.RpcUtils;

public abstract class AbstractRpcOioWriter extends AbstractRpcWriter{
	
	public AbstractRpcOioWriter(){
		super();
	}
	
	public boolean exeSend(AbstractRpcConnector con){
		boolean hasSend = false;
		RpcOioConnector connector = (RpcOioConnector)con;
		DataOutputStream dos = connector.getOutputStream();
		while(connector.isNeedToSend()){
			RpcObject rpc = connector.getToSend();
			RpcUtils.writeDataRpc(rpc, dos,connector);
			hasSend = true;
		}
		return hasSend;
	}
	
}
