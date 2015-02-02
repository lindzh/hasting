package com.linda.framework.rpc.utils;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

import org.apache.log4j.Logger;

import com.linda.framework.rpc.RpcObject;
import com.linda.framework.rpc.exception.RpcException;
import com.linda.framework.rpc.utils.RpcUtils.RpcType;

public class NioUtils {
	
	private static Logger logger = Logger.getLogger(NioUtils.class);
	
	public static final int RPC_PROTOCOL_HEAD_LEN = 20;

	public static void clearNioWriteOp(SelectionKey key){
		if(checkKey(key)){
			final int interestOps = key.interestOps();
			if ((interestOps & SelectionKey.OP_WRITE) != 0) {
				key.interestOps(interestOps & ~SelectionKey.OP_WRITE);
			}
		}
	}
	
	public static void setNioWriteOp(SelectionKey key){
		if(checkKey(key)){
	        final int interestOps = key.interestOps();
	        if ((interestOps & SelectionKey.OP_WRITE) == 0) {
	            key.interestOps(interestOps | SelectionKey.OP_WRITE);
	        }
		}
	}
	
	public static void clearNioReadOp(SelectionKey key){
		if (checkKey(key)) {
			int interestOps = key.interestOps();
			if ((interestOps & SelectionKey.OP_READ) != 0) {
				key.interestOps(interestOps & ~SelectionKey.OP_READ);
			}
		}
	}
	
	public static void setNioReadOp(SelectionKey key){
		if(checkKey(key)){
	        final int interestOps = key.interestOps();
	        if ((interestOps & SelectionKey.OP_READ) == 0) {
	        	key.interestOps(interestOps | SelectionKey.OP_READ);
	        }
		}
	}
	
	private static boolean checkKey(SelectionKey key){
        if (!key.isValid()) {
        	logger.info("valid selection key");
            return false;
        }
        logger.info("selection key ok");
        return true;
	}
	
	/**
	 * type|threadId|index|length|data
	 * @return
	 */
	public static boolean writeBuffer(ByteBuffer buffer,RpcObject object){
		if (object.getLength() > RpcUtils.MEM_1M) {
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
		if (object.getLength() > RpcUtils.MEM_1M) {
			throw new RpcException("rpc data too long "+ object.getLength());
		}
		if(object.getLength()>0){
			byte[] buf = new byte[object.getLength()];
			buffer.get(buf, 0, buf.length);
			object.setData(buf);
		}
		return object;
	}

}
