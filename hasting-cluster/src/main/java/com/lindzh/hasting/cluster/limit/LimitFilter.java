package com.lindzh.hasting.cluster.limit;

import com.lindzh.hasting.rpc.RemoteCall;
import com.lindzh.hasting.rpc.RpcContext;
import com.lindzh.hasting.rpc.RpcObject;
import com.lindzh.hasting.rpc.exception.RpcException;
import com.lindzh.hasting.rpc.filter.RpcFilter;
import com.lindzh.hasting.rpc.filter.RpcFilterChain;
import com.lindzh.hasting.rpc.net.RpcSender;
import org.apache.log4j.Logger;

/**
 * Created by lin on 2017/1/24.
 * 限流
 */
public class LimitFilter implements RpcFilter {

    private Logger logger = Logger.getLogger("rpcCluster");

    private LimitCache limitCache;

    public LimitFilter(LimitCache limitCache){
        this.limitCache = limitCache;
    }

    @Override
    public void doFilter(RpcObject rpc, RemoteCall call, RpcSender sender, RpcFilterChain chain) {
        if(limitCache!=null){
            String application = (String)RpcContext.getContext().getAttachment("Application");
            boolean accept = limitCache.accept(application, call.getService(), call.getMethod());
            if(accept){
                chain.nextFilter(rpc, call, sender);
            }else{
                throw new RpcException("request limited ,service is too busy");
            }
        }
    }
}
