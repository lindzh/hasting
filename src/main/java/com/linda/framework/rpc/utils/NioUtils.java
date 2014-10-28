package com.linda.framework.rpc.utils;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

import org.apache.log4j.Logger;

import com.linda.framework.rpc.RpcObject;
import com.linda.framework.rpc.exception.RpcException;
import com.linda.framework.rpc.utils.RpcUtils.RpcType;

public class NioUtils {
	
	private static Logger logger = Logger.getLogger(NioUtils.class);

	public static void clearNioWriteOp(SelectionKey key){
		if (!key.isValid()) {
			return;
		}
		final int interestOps = key.interestOps();
		if ((interestOps & SelectionKey.OP_WRITE) != 0) {
			key.interestOps(interestOps & ~SelectionKey.OP_WRITE);
		}
	}
	
	public static void setNioWriteOp(SelectionKey key){
        if (!key.isValid()) {
            return;
        }
        final int interestOps = key.interestOps();
        if ((interestOps & SelectionKey.OP_WRITE) == 0) {
            key.interestOps(interestOps | SelectionKey.OP_WRITE);
        }
	}
	
	public static void clearNioReadOp(SelectionKey key){
		if (!key.isValid()) {
			return;
		}
		int interestOps = key.interestOps();
		if ((interestOps & SelectionKey.OP_READ) != 0) {
			key.interestOps(interestOps & ~SelectionKey.OP_READ);
		}
	}
	
	public static void setNioReadOp(SelectionKey key){
        if (!key.isValid()) {
            return;
        }
        final int interestOps = key.interestOps();
        if ((interestOps & SelectionKey.OP_READ) == 0) {
        	key.interestOps(interestOps | SelectionKey.OP_READ);
        }
	}
	
	/**
	 * type|threadId|index|length|data
	 * @return
	 */
	public static boolean writeBuffer(ByteBuffer buffer,RpcObject object){
		if (object.getLength() > RpcUtils.MEM_2M) {
			throw new RpcException("rpc data too long "+ object.getLength());
		}
		buffer.putInt(object.getType().getType());
		buffer.putLong(object.getThreadId());
		buffer.putInt(object.getIndex());
		buffer.putInt(object.getLength());
		buffer.put(object.getData());
		return true;
	}
	
	public static void logBuffer(String clazz,String key,ByteBuffer buffer){
		logger.info(clazz+" "+key+" buff position:"+buffer.position()+" limit:"+buffer.limit()+" capacity:"+buffer.capacity());
	}
	
	public static RpcObject readBuffer(ByteBuffer buffer){
		RpcObject object = new RpcObject();
		object.setType(RpcType.getByType(buffer.getInt()));
		object.setThreadId(buffer.getLong());
		object.setIndex(buffer.getInt());
		object.setLength(buffer.getInt());
		if (object.getLength() > RpcUtils.MEM_2M) {
			throw new RpcException("rpc data too long "+ object.getLength());
		}
		byte[] buf = new byte[object.getLength()];
		buffer.get(buf, 0, buf.length);
		object.setData(buf);
		return object;
	}
	
	

}
