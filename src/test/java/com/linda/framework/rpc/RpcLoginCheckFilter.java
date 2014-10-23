package com.linda.framework.rpc;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.linda.framework.rpc.RemoteCall;
import com.linda.framework.rpc.RpcException;
import com.linda.framework.rpc.RpcFilter;
import com.linda.framework.rpc.RpcFilterChain;
import com.linda.framework.rpc.RpcObject;
import com.linda.framework.rpc.RpcSend;

public class RpcLoginCheckFilter implements RpcFilter{
	
	private Logger logger = Logger.getLogger(RpcLoginCheckFilter.class);
	
	@Override
	public void doFilter(RpcObject rpc, RemoteCall call, RpcSend sender,RpcFilterChain chain) {
		String service = call.getService();
		if(service.equals(LoginRpcService.class.getName())){
			logger.info("----------user login---------------");
			try{
				chain.nextFilter(rpc, call, sender);
				rpc.getRpcContext().put("logined", true);
				rpc.getRpcContext().put("user", call.getArgs());
			}catch(RpcException e){
				throw e;
			}
			return;
		}else{
			ConcurrentHashMap<String,Object> context = rpc.getRpcContext();
			if(context.get("logined")==null){
				throw new RpcException("user not login");
			}else{
				chain.nextFilter(rpc, call, sender);
			}
		}
	}

}
