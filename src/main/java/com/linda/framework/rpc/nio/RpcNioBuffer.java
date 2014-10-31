package com.linda.framework.rpc.nio;

import java.util.Arrays;

import com.linda.framework.rpc.RpcObject;
import com.linda.framework.rpc.exception.RpcException;
import com.linda.framework.rpc.utils.NioUtils;
import com.linda.framework.rpc.utils.RpcUtils;
import com.linda.framework.rpc.utils.RpcUtils.RpcType;


public class RpcNioBuffer{
	
	private byte[] buf;
	private int readIndex;
	private int writeIndex;
	
    public RpcNioBuffer() {
        this(32);
    }
    
    public RpcNioBuffer(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Negative initial size: " + size);
        }
        buf = new byte[size];
    }

	private void ensureCapacity(int minCapacity) {
		if (minCapacity - buf.length > 0)
			grow(minCapacity);
	}

	private void grow(int minCapacity) {
		int oldCapacity = buf.length;
		int newCapacity = oldCapacity << 1;
		if (newCapacity - minCapacity < 0)
			newCapacity = minCapacity;
		if (newCapacity < 0) {
			if (minCapacity < 0)
				throw new OutOfMemoryError();
			newCapacity = Integer.MAX_VALUE;
		}
		buf = Arrays.copyOf(buf, newCapacity);
	}
	
    public void write(byte b[], int off, int len) {
        if ((off < 0) || (off > b.length) || (len < 0) ||
            ((off + len) - b.length > 0)) {
            throw new IndexOutOfBoundsException();
        }
        ensureCapacity(writeIndex + len);
        System.arraycopy(b, off, buf, writeIndex, len);
        writeIndex += len;
    }
    
    public void write(byte b[]) {
    	this.write(b, 0, b.length);
    }
    
    public void reset() {
    	writeIndex = 0;
    }
    
    public byte toByteArray()[] {
    	return Arrays.copyOfRange(buf, readIndex, writeIndex);
    }
    
    public String toString() {
        return new String(buf, readIndex, writeIndex);
    }
	
    public synchronized int size() {
        return writeIndex-readIndex;
    }
    
    public void compact(){
    	if(readIndex>0){
        	for(int i=readIndex;i<writeIndex;i++){
        		buf[i-readIndex] = buf[i];
        	}
        	writeIndex=writeIndex-readIndex;
        	readIndex = 0;
    	}
    }
    
    public boolean hasRpcObject(){
    	if(writeIndex-readIndex>NioUtils.RPC_PROTOCOL_HEAD_LEN){
    		byte[] lenBuf = new byte[4];
    		System.arraycopy(buf, readIndex+16, lenBuf, 0, 4);
    		int len = RpcUtils.bytesToInt(lenBuf);
    		if(writeIndex-readIndex>=NioUtils.RPC_PROTOCOL_HEAD_LEN+len){
    			return true;
    		}
    	}
    	return false;
    }
    
    public void writeInt(int i){
    	byte[] bytes = RpcUtils.intToBytes(i);
    	this.write(bytes);
    }
    
    public void writeLong(long v){
    	byte[] bytes = RpcUtils.longToBytes(v);
    	this.write(bytes);
    }
    
    public int readInt(){
		byte[] intBuf = new byte[4];
		System.arraycopy(buf, readIndex, intBuf, 0, 4);
		readIndex += 4;
		return RpcUtils.bytesToInt(intBuf);
    }
    
    public long readLong(){
		byte[] longBuf = new byte[8];
		System.arraycopy(buf, readIndex, longBuf, 0, 8);
		readIndex += 8;
		return RpcUtils.bytesToLong(longBuf);
    }
    
    public byte[] readBytes(int len){
		byte[] byteBuf = new byte[len];
		System.arraycopy(buf, readIndex, byteBuf, 0, len);
		readIndex += len;
		return byteBuf;
    }
    

    public byte[] readBytes(){
    	int len = writeIndex-readIndex;
		byte[] byteBuf = new byte[len];
		System.arraycopy(buf, readIndex, byteBuf, 0, len);
		readIndex+=len;
		if(readIndex>buf.length/2){
			this.compact();
		}
		return byteBuf;
    }
    
    public void writeRpcObject(RpcObject rpc){
    	this.writeInt(rpc.getType().getType());
    	this.writeLong(rpc.getThreadId());
    	this.writeInt(rpc.getIndex());
    	this.writeInt(rpc.getLength());
    	this.write(rpc.getData());
    }
    
    public RpcObject readRpcObject(){
    	RpcObject rpc = new RpcObject();
		int type = this.readInt();
		rpc.setType(RpcType.getByType(type));
		rpc.setThreadId(this.readLong());
		rpc.setIndex(this.readInt());
		rpc.setLength(this.readInt());
		if (rpc.getLength() > 0) {
			if (rpc.getLength() > RpcUtils.MEM_2M) {
				throw new RpcException("rpc data too long "	+ rpc.getLength());
			}
			rpc.setData(this.readBytes(rpc.getLength()));
		}
		if(readIndex>buf.length/2){
			this.compact();
		}
		return rpc;
    }
}
