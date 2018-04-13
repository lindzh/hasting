package com.lindzh.hasting.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import com.lindzh.hasting.rpc.RpcObject;
import com.lindzh.hasting.rpc.exception.RpcException;
import com.lindzh.hasting.rpc.utils.RpcUtils;


/**
 * 
 * @author linda
 * RPC Object encoder
 * @See RpcUtils
 *
 */
public class RpcNettyEncoder extends MessageToByteEncoder<RpcObject>{

	@Override
	protected void encode(ChannelHandlerContext ctx, RpcObject msg, ByteBuf out)
			throws Exception {
		out.writeInt(msg.getType().getType());
		out.writeLong(msg.getThreadId());
		out.writeInt(msg.getIndex());
		out.writeInt(msg.getLength());
		if(msg.getLength()>0){
			if(msg.getLength()>RpcUtils.MEM_1M){
				throw new RpcException("rpc data too long "+ msg.getLength());
			}
			out.writeBytes(msg.getData());
		}
	}
	
}
