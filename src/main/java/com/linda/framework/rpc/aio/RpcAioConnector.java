package com.linda.framework.rpc.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import com.linda.framework.rpc.RpcObject;
import com.linda.framework.rpc.exception.RpcException;
import com.linda.framework.rpc.net.AbstractRpcConnector;
import com.linda.framework.rpc.nio.RpcNioBuffer;
import com.linda.framework.rpc.utils.RpcUtils;

/**
 * 添加异步aio支持 java7
 * @author lindezhi
 * 默认使用32kb，实际长度为压缩过的32kb
 */
public class RpcAioConnector extends AbstractRpcConnector {
	
	private AsynchronousSocketChannel channel;
	
	private ByteBuffer readBuf;
	
	private ByteBuffer writeBuf;
	
	private RpcNioBuffer nioReadBuffer;
	
	private RpcNioBuffer nioWriteBuffer;
	
	private RpcReadCompletionHandler readHandler;
	
	private RpcWriteCompletionHandler writeHandler;
	
	private AsynchronousChannelGroup channelGroup;
	
	private AtomicBoolean inWrite = new AtomicBoolean(false);
	
	private Logger logger = Logger.getLogger(RpcAioConnector.class);
	
	//默认channel group threads 为 5
	private int channelGroupThreads = 5;
	
	public RpcAioConnector(RpcAioWriter writer,AsynchronousSocketChannel channel){
		super(writer);
		this.channel = channel;
		this.initBuf();
	}
	
	public RpcAioConnector(){
		this(new RpcAioWriter(),null);
	}
	
	/**
	 * 读写缓冲池
	 */
	private void initBuf(){
		writeBuf = ByteBuffer.allocate(RpcUtils.MEM_512KB);
		readBuf = ByteBuffer.allocate(RpcUtils.MEM_512KB);
		nioReadBuffer = new RpcNioBuffer(RpcUtils.MEM_512KB);
		nioWriteBuffer = new RpcNioBuffer(RpcUtils.MEM_512KB);
	}
	
	/**
	 * 如果是consumer，readwritehandler需要自己初始化，如果是acceptor则缓冲池使用公共的，避免多次初始化
	 */
	private void checkWriter(){
		if(this.getRpcWriter()==null){
			this.setRpcWriter(new RpcAioWriter());
		}
		//检查writer
		if(this.writeHandler==null){
			writeHandler = new RpcWriteCompletionHandler();
		}
		if(this.readHandler==null){
			readHandler = new RpcReadCompletionHandler();
		}
	}
	
	/**
	 * 通道异步线程池
	 */
	private void checkChannelGroup(){
		//检查group
		if(channelGroup==null){
			try {
				channelGroup = AsynchronousChannelGroup.withThreadPool(Executors.newFixedThreadPool(channelGroupThreads));
			} catch (IOException e) {
				throw new RpcException(e);
			}
		}
	}
	
	@Override
	public void startService() {
		super.startService();
		//检查writer
		this.checkWriter();
		try{
			//如果TCP连接已经建立，不需要建立连接，如acceptor，如果是服务消费者，则直接跳过
			if(channel==null){
				this.checkChannelGroup();
				channel = AsynchronousSocketChannel.open(channelGroup);
				channel.connect(new InetSocketAddress(this.getHost(),this.getPort()));
				logger.info("connect to "+this.getHost()+":"+this.getPort()+" success");
			}
			//JDK7
			InetSocketAddress remoteAddress = (InetSocketAddress)channel.getRemoteAddress();
			InetSocketAddress localAddress = (InetSocketAddress)channel.getLocalAddress();

			String remote = RpcUtils.genAddressString("remoteAddress-> ", remoteAddress);
			String local = RpcUtils.genAddressString("localAddress-> ", localAddress);
			logger.info(local+"  "+remote);
			remotePort = remoteAddress.getPort();
			remoteHost = remoteAddress.getAddress().getHostAddress();
			//注册上去
			this.getRpcWriter().registerWrite(this);
			this.getRpcWriter().startService();
			
			this.fireStartNetListeners();
			//start read
			this.channel.read(readBuf, this, readHandler);
		}catch(IOException e){
			logger.error("connect to host "+this.getHost()+" port "+this.getPort()+" failed", e);
			throw new RpcException("connect to host error");
		}
	}

	public void setReadHandler(RpcReadCompletionHandler readHandler) {
		this.readHandler = readHandler;
	}

	public void setWriteHandler(RpcWriteCompletionHandler writeHandler) {
		this.writeHandler = writeHandler;
	}

	/**
	 * 数据读取回调
	 * @param num
	 */
	public void readCallback(int num){
		if(num<1){
			if(num<0){
				//num == -1 表示连接已经关闭，需要关闭当前连接
				this.handleConnectorException(new RpcException("connection closed"));
			}else{
				//没有接受到数据，继续接收数据
				this.channel.read(readBuf, this, readHandler);
			}
		}else{
			//接受到了数据，将数据放到buffer中
			readBuf.flip();
			byte[] readBytes = new byte[num];
			readBuf.get(readBytes);
			nioReadBuffer.write(readBytes);
			readBuf.clear();
			//如果二进制数据中有rpc对象，提交到上层，否则，保存当前数据，继续接受，否则可能读取到部分数据，导致数据不全，rpcobject不全
			while(nioReadBuffer.hasRpcObject()){
				//读取rpc对象提交到上层
				RpcObject rpc = nioReadBuffer.readRpcObject();
				this.fireCall(rpc);
			}
			this.channel.read(readBuf, this, readHandler);
		}
	}
	
	/**
	 * 数据写回调
	 * @param num
	 */
	public void writeCallback(int num){
		//老的数据没有发送完毕，继续发送
		if(this.writeBuf.hasRemaining()){
			channel.write(writeBuf, this, writeHandler);
		}else{
			//检查有无新数据，无数据不发送
			writeBuf.clear();
			//检测是否需要写可以写
			if(this.isNeedToSend()){
				RpcObject rpc = this.getToSend();
				nioWriteBuffer.writeRpcObject(rpc);
				writeBuf.put(nioWriteBuffer.readBytes());
				writeBuf.flip();
				channel.write(writeBuf, this, writeHandler);
			}else{
				inWrite.compareAndSet(true, false);
			}
		}
	}
	
	/**
	 * 执行发送
	 */
	public void exeSend(){
		//需要发送数据
		if(!inWrite.get()&&this.isNeedToSend()){
			inWrite.compareAndSet(false, true);
			RpcObject rpc = this.getToSend();
			nioWriteBuffer.writeRpcObject(rpc);
			writeBuf.put(nioWriteBuffer.readBytes());
			writeBuf.flip();
			channel.write(writeBuf, this, writeHandler);
		}
	}
	
	/**
	 * 关闭连接，清空buff
	 */
	@Override
	public void stopService() {
		super.stopService();
		this.getRpcWriter().unRegWrite(this);
		this.sendQueueCache.clear();
		this.rpcContext.clear();
		try {
			channel.close();
			writeBuf.clear();
			readBuf.clear();
			nioReadBuffer.clear();
			nioWriteBuffer.clear();
		} catch (IOException e) {
			//
		}
		this.stop = true;
	}
	
	@Override
	public void handleConnectorException(Exception e) {
		this.getRpcWriter().unRegWrite(this);
		this.stopService();
		logger.error("connection caught io exception close");
		if(e instanceof RpcException){
			throw (RpcException)e;
		}else{
			throw new RpcException(e);
		}
	}
	
	public void handleFail(Throwable e, RpcAioConnector connector){
		connector.handleNetException(new RpcException(e));
	}

	public int getChannelGroupThreads() {
		return channelGroupThreads;
	}

	public void setChannelGroupThreads(int channelGroupThreads) {
		this.channelGroupThreads = channelGroupThreads;
	}
}
