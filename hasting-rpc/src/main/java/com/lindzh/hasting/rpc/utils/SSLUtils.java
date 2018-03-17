package com.lindzh.hasting.rpc.utils;

import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.apache.log4j.Logger;

import com.lindzh.hasting.rpc.exception.RpcException;

/**
 * ssl的方式，目前不支持
 * @author lindezhi
 * 2016年6月13日 下午4:29:59
 */
public class SSLUtils {

	private static Logger logger = Logger.getLogger(SSLUtils.class);
	
	public static Socket getSocketInstance(SSLContext sslContext,int sslmode){
		Socket socket = null;
		try{
			if(sslContext!=null){
				SSLSocketFactory factory = sslContext.getSocketFactory();
				socket = factory.createSocket();
				postSSLSocket((SSLSocket)socket,sslmode);
			}
		}catch(Exception e){
			logger.error("create ssl socket error,start to use socket");
		}
		if(socket==null){
			socket = new Socket();
		}
		return socket;
	}
	
	public static void postSSLSocket(SSLSocket socket,int sslmode){
        String[] pwdsuits = socket.getSupportedCipherSuites();  
        socket.setEnabledCipherSuites(pwdsuits);
        if(sslmode == 2){//双向认证  
            socket.setUseClientMode(false);  
            socket.setNeedClientAuth(true);  
        }else{  
            socket.setUseClientMode(true);  
            socket.setWantClientAuth(true);  
        } 
	}
	
	private static void postSSLServerSocket(SSLServerSocket sslServerSocket,int sslmode){
	       String[] pwdsuits = sslServerSocket.getSupportedCipherSuites();  
	        sslServerSocket.setEnabledCipherSuites(pwdsuits);  
	        sslServerSocket.setUseClientMode(false);  
	        if(sslmode == 2){  
	            sslServerSocket.setNeedClientAuth(true);  
	        }else{  
	            sslServerSocket.setWantClientAuth(true);  
	        }
	}
	
	public static ServerSocket getServerSocketInstance(SSLContext sslContext,int sslmode){
		ServerSocket serverSocket = null;
		try{
			if(sslContext!=null){
				SSLServerSocketFactory factory = sslContext.getServerSocketFactory();
				SSLServerSocket sslServerSocket = (SSLServerSocket)factory.createServerSocket();
				postSSLServerSocket(sslServerSocket,sslmode);
				sslServerSocket.setReuseAddress(true);
			}
		}catch(Exception e){
			logger.error("create ssl server error,start to use socket");
		}
		try{
			if(serverSocket==null){
				serverSocket = new ServerSocket();
			}
		}catch(Exception e){
			throw new RpcException(e);
		}
		return serverSocket;
	}
}
