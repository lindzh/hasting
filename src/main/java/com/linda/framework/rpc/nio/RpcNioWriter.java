package com.linda.framework.rpc.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.linda.framework.rpc.RpcObject;
import com.linda.framework.rpc.net.AbstractRpcConnector;
import com.linda.framework.rpc.net.AbstractRpcWriter;
import com.linda.framework.rpc.utils.NioUtils;

public class RpcNioWriter extends AbstractRpcWriter{

	@Override
	public boolean doSend(AbstractRpcConnector con) {
		boolean result = false;
		RpcNioConnector connector = (RpcNioConnector)con;
		SocketChannel channel = connector.getChannel();
		while(connector.isNeedToSend()){
			ByteBuffer buffer = connector.getWriteBuf();
			RpcObject rpc = connector.getToSend();
			NioUtils.writeBuffer(buffer,rpc);
			buffer.flip();
			try {
				channel.write(buffer);
			} catch (IOException e) {
				e.printStackTrace();
			}
			buffer.clear();
			result=true;
		}
		return result;
	}

}
