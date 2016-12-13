package com.linda.framework.rpc.cluster;

import com.linda.framework.rpc.RemoteCall;
import com.linda.framework.rpc.RpcObject;
import com.linda.framework.rpc.filter.RpcFilter;
import com.linda.framework.rpc.filter.RpcFilterChain;
import com.linda.framework.rpc.net.RpcSender;
import com.linda.framework.rpc.utils.RpcUtils;

import java.util.Map;

/**
 * Created by lin on 2016/12/8.
 * provider端token验证
 */
public class TokenFilter implements RpcFilter{

    private int timeout;

    /**
     * 当前服务器token
     */
    private String token;

    /**
     * 是否校验token
     */
    private boolean validateToken;

    public TokenFilter(int timeout,String token,boolean validateToken){
        this.timeout = timeout;
        this.token = token;
        this.validateToken = validateToken;
    }

    @Override
    public void doFilter(RpcObject rpc, RemoteCall call, RpcSender sender, RpcFilterChain chain) {
        if(this.validateToken){
            Map<String, Object> attachment = call.getAttachment();
            if(attachment==null){
                this.sendTokenInvalid(rpc,call,sender,"attachment RpcToken container attachment null");
                return ;
            }
            String token = (String)attachment.get("RpcToken");
            if(token==null){
                this.sendTokenInvalid(rpc,call,sender,"attachment RpcToken is null");
                return ;
            }
            if(!token.equals(this.token)){
                this.sendTokenInvalid(rpc,call,sender,"attachment RpcToken not equal");
                return ;
            }
            chain.nextFilter(rpc, call, sender);

        }else{
            chain.nextFilter(rpc, call, sender);
        }

    }

    private void sendTokenInvalid(RpcObject rpc, RemoteCall call, RpcSender sender,String message){
        RpcObject respRpc = new RpcObject(0,rpc.getIndex(), 0, null);
        respRpc.setThreadId(rpc.getThreadId());
        respRpc.setType(RpcUtils.RpcType.FAIL);
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
