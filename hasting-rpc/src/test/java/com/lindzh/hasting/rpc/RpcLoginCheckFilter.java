package com.lindzh.hasting.rpc;

import java.util.Map;

import com.lindzh.hasting.rpc.exception.RpcException;
import com.lindzh.hasting.rpc.filter.RpcFilter;
import com.lindzh.hasting.rpc.filter.RpcFilterChain;
import com.lindzh.hasting.rpc.net.RpcSender;
import org.apache.log4j.Logger;

public class RpcLoginCheckFilter implements RpcFilter {
	
	private Logger logger = Logger.getLogger(RpcLoginCheckFilter.class);
	
	@Override
	public void doFilter(RpcObject rpc, RemoteCall call, RpcSender sender, RpcFilterChain chain) {
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
			Map<String,Object> context = rpc.getRpcContext();
			if(context.get("logined")==null){
				throw new RpcException("user not login");
			}else{
				chain.nextFilter(rpc, call, sender);
			}
		}
	}

}
