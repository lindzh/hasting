package com.linda.framework.rpc.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.linda.framework.rpc.RemoteCall;
import com.linda.framework.rpc.RpcObject;
import com.linda.framework.rpc.exception.RpcException;
import com.linda.framework.rpc.exception.RpcExceptionHandler;

public class RpcUtils {

	private static Logger logger = Logger.getLogger(RpcUtils.class);
	private static Map<String, Method> methodCache = new HashMap<String, Method>();

	public static int MEM_2M = 1024 * 1024 * 2;

	public static void writeRpc(RpcObject rpc, OutputStream dos) {
		try {
			dos.write(rpc.getType().getType());
			dos.write(RpcUtils.longToBytes(rpc.getThreadId()));
			dos.write(RpcUtils.intToBytes(rpc.getIndex()));
			dos.write(RpcUtils.intToBytes(rpc.getLength()));
			if (rpc.getLength() > 0) {
				if (rpc.getLength() > MEM_2M) {
					throw new RpcException("rpc data too long "
							+ rpc.getLength());
				}
				dos.write(rpc.getData());
			}
			dos.flush();
		} catch (IOException e) {
			throw new RpcException(e);
		}
	}
	
	public static void writeDataRpc(RpcObject rpc, DataOutputStream dos) {
		try {
			dos.writeInt(rpc.getType().getType());
			dos.writeLong(rpc.getThreadId());
			dos.writeInt(rpc.getIndex());
			dos.writeInt(rpc.getLength());
			if (rpc.getLength() > 0) {
				if (rpc.getLength() > MEM_2M) {
					throw new RpcException("rpc data too long "+ rpc.getLength());
				}
				dos.write(rpc.getData());
			}
			dos.flush();
		} catch (IOException e) {
			throw new RpcException(e);
		}
	}

	public static RpcObject readRpc(InputStream dis, byte[] buffer) {
		try {
			RpcObject rpc = new RpcObject();
			int type = dis.read();
			rpc.setType(RpcType.getByType(type));
			byte[] thBytes = new byte[8];
			dis.read(thBytes);
			rpc.setThreadId(RpcUtils.bytesToLong(thBytes));
			byte[] indexBytes = new byte[4];
			dis.read(indexBytes);
			rpc.setIndex(RpcUtils.bytesToInt(indexBytes));
			byte[] lenBytes = new byte[4];
			dis.read(lenBytes);
			rpc.setLength(RpcUtils.bytesToInt(lenBytes));
			if (rpc.getLength() > 0) {
				if (rpc.getLength() > MEM_2M) {
					throw new RpcException("rpc data too long "
							+ rpc.getLength());
				}
				byte[] buf = new byte[rpc.getLength()];
				dis.read(buf);
				rpc.setData(buf);
			}
			return rpc;
		} catch (IOException e) {
			throw new RpcException(e);
		}
	}
	
	public static RpcObject readDataRpc(DataInputStream dis) {
		try {
			RpcObject rpc = new RpcObject();
			rpc.setType(RpcType.getByType(dis.readInt()));
			rpc.setThreadId(dis.readLong());
			rpc.setIndex(dis.readInt());
			rpc.setLength(dis.readInt());
			if (rpc.getLength() > 0) {
				if (rpc.getLength() > MEM_2M) {
					throw new RpcException("rpc data too long "+ rpc.getLength());
				}
				byte[] buf = new byte[rpc.getLength()];
				dis.read(buf);
				rpc.setData(buf);
			}
			return rpc;
		} catch (IOException e) {
			throw new RpcException(e);
		}
	}

	public static void close(DataInputStream dis, DataOutputStream dos) {
		try {
			dis.close();
			dos.close();
		} catch (IOException e) {
			throw new RpcException(e);
		}
	}
	
	/**
	 * type|threadId|index|length|data
	 * @return
	 */
	public static boolean writeBuffer(ByteBuffer buffer,RpcObject object){
		if (object.getLength() > MEM_2M) {
			throw new RpcException("rpc data too long "+ object.getLength());
		}
		buffer.putInt(object.getType().getType());
		buffer.putLong(object.getThreadId());
		buffer.putInt(object.getIndex());
		buffer.putInt(object.getLength());
		buffer.put(object.getData());
		return true;
	}
	
	public static RpcObject readBuffer(ByteBuffer buffer){
		RpcObject object = new RpcObject();
		object.setType(RpcType.getByType(buffer.getInt()));
		object.setThreadId(buffer.getLong());
		object.setIndex(buffer.getInt());
		object.setLength(buffer.getInt());
		if (object.getLength() > MEM_2M) {
			throw new RpcException("rpc data too long "+ object.getLength());
		}
		byte[] buf = new byte[object.getLength()];
		buffer.get(buf, 0, buf.length);
		object.setData(buf);
		return object;
	}

	public static Object invokeMethod(Object obj, String methodName,Object[] args,RpcExceptionHandler exceptionHandler) {
		Class<? extends Object> clazz = obj.getClass();
		String key = clazz.getCanonicalName() + "." + methodName;
		Method method = methodCache.get(key);
		if (method == null) {
			method = RpcUtils.findMethod(clazz, methodName, args);
			if (method == null) {
				throw new RpcException("method not exist method:" + methodName);
			}
			methodCache.put(key, method);
		}
		return RpcUtils.invoke(method, obj, args,exceptionHandler);
	}

	public static Object invoke(Method method, Object obj, Object[] args,RpcExceptionHandler exceptionHandler) {
		try {
			return method.invoke(obj, args);
		} catch (IllegalAccessException e) {
			throw new RpcException("IllegalAccess request access error");
		} catch (IllegalArgumentException e) {
			throw new RpcException("IllegalArgument request param wrong");
		} catch (InvocationTargetException e) {
			if(e.getCause()!=null){
				exceptionHandler.handleException(null, null, e.getCause());
			}else{
				exceptionHandler.handleException(null, null, e);
			}
			throw new RpcException("rpc invoke target error");
		}
	}

	public static void handleException(RpcExceptionHandler rpcExceptionHandler,RpcObject rpc,RemoteCall call,Exception e){
		if(rpcExceptionHandler!=null){
			rpcExceptionHandler.handleException(rpc,call,e);
		}else{
			logger.error("exceptionHandler null exception message:"+e.getMessage());
		}
	}
	
	public static long getNowInmilliseconds() {
		return new Date().getTime();
	}

	public static byte[] intToBytes(int iSource) {
		byte[] bLocalArr = new byte[4];
		for (int i = 0; (i < 4); i++) {
			bLocalArr[i] = (byte) (iSource >> 8 * i & 0xFF);
		}
		return bLocalArr;
	}

	public static int bytesToInt(byte[] bRefArr) {
		int iOutcome = 0;
		byte bLoop;
		for (int i = 0; i < bRefArr.length; i++) {
			bLoop = bRefArr[i];
			iOutcome += (bLoop & 0xFF) << (8 * i);
		}
		return iOutcome;
	}

	public static byte[] longToBytes(long number) {
		long temp = number;
		byte[] b = new byte[8];
		for (int i = 0; i < b.length; i++) {
			b[i] = new Long(temp & 0xff).byteValue();
			temp = temp >> 8;
		}
		return b;
	}

	public static long bytesToLong(byte[] b) {
		long s = 0;
		long s0 = b[0] & 0xff;
		long s1 = b[1] & 0xff;
		long s2 = b[2] & 0xff;
		long s3 = b[3] & 0xff;
		long s4 = b[4] & 0xff;
		long s5 = b[5] & 0xff;
		long s6 = b[6] & 0xff;
		long s7 = b[7] & 0xff;
		s1 <<= 8;
		s2 <<= 16;
		s3 <<= 24;
		s4 <<= 8 * 4;
		s5 <<= 8 * 5;
		s6 <<= 8 * 6;
		s7 <<= 8 * 7;
		s = s0 | s1 | s2 | s3 | s4 | s5 | s6 | s7;
		return s;
	}
	
	public static String bytesToHexString(byte[] bytes){   
	    StringBuilder stringBuilder = new StringBuilder();   
	    if (bytes == null || bytes.length <= 0) {   
	        return stringBuilder.toString();   
	    }   
	    for (int i = 0; i < bytes.length; i++) {   
	        int v = bytes[i] & 0xFF;   
	        String hv = Integer.toHexString(v);   
	        if (hv.length() < 2) {   
	            stringBuilder.append(0);   
	        }   
	        stringBuilder.append(hv);   
	    }   
	    return stringBuilder.toString();   
	} 

	public static Method findMethod(Class clazz, String name, Object[] args) {
		Method[] methods = clazz.getMethods();
		for (Method m : methods) {
			if (m.getName().equals(name)) {
				return m;
			}
		}
		return null;
	}

	public enum RpcType {
		ONEWAY(1), INVOKE(2), SUC(3), FAIL(4);
		private int type;

		RpcType(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}

		public static RpcType getByType(int type) {
			RpcType[] values = RpcType.values();
			for (RpcType v : values) {
				if (v.type == type) {
					return v;
				}
			}
			return ONEWAY;
		}
	}

}
