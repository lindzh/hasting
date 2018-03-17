package com.lindzh.hasting.cluster;

import com.lindzh.hasting.rpc.RpcService;
import com.lindzh.hasting.rpc.cluster1.RpcHostAndPort;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

public class MD5Utils {
    private static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7',
                                    '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    public static String hostMd5(String application,String host,int port){
        return md5(application+"_"+host+"_"+port);
    }

    public static String serviceMd5(RpcService service){
        return md5(service.getGroup()+"_"+service.getName()+"_"+service.getVersion());
    }

    public static String hostMd5(RpcHostAndPort host){
        return md5(host.getApplication()+"_"+host.getHost()+"_"+host.getPort());
    }

    public static String md5(String source) {
		try {
			byte[] bytes = md5(source.getBytes("utf-8"));
	        char str[] = new char[bytes.length * 2];
	        int k = 0;
	        for (int i = 0; i < bytes.length; i++) {
	            byte byte0 = bytes[i];
	            str[k++] = hexDigits[byte0 >>> 4 & 0xf];
	            str[k++] = hexDigits[byte0 & 0xf];
	        }
	        return new String(str);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
    }

    private static byte[] md5(byte[] source) {
        byte[] result = new byte[0];
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(source);
            result = md.digest();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
