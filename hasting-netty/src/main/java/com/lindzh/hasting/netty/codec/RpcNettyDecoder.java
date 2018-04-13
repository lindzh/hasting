package com.lindzh.hasting.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import com.lindzh.hasting.rpc.RpcObject;
import com.lindzh.hasting.rpc.exception.RpcException;
import com.lindzh.hasting.rpc.utils.NioUtils;
import com.lindzh.hasting.rpc.utils.RpcUtils;
import com.lindzh.hasting.rpc.utils.RpcUtils.RpcType;

public class RpcNettyDecoder extends ByteToMessageDecoder{

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		int readableBytes = in.readableBytes();
		if(readableBytes>=NioUtils.RPC_PROTOCOL_HEAD_LEN){
			in.markReaderIndex();
			int type = in.readInt();//type
			long threadId = in.readLong();//threadId
			int index = in.readInt();//index
			int length = in.readInt();// data length
			if(length>RpcUtils.MEM_1M){
				throw new RpcException("rpc data too long "+ length);
			}
			if(in.readableBytes()>=length){
				byte[] buf = new byte[length];
				if(length>0){
					in.readBytes(buf);
				}
				RpcObject rpc = new RpcObject();
				rpc.setType(RpcType.getByType(type));
				rpc.setThreadId(threadId);
				rpc.setIndex(index);
				rpc.setLength(length);
				rpc.setData(buf);
				out.add(rpc);
			}else{
				in.resetReaderIndex();
			}
		}
	}
}
