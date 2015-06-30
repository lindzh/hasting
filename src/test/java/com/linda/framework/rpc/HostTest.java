package com.linda.framework.rpc;

import java.util.List;

import com.linda.framework.rpc.utils.RpcUtils;

public class HostTest {
	
	public static void main(String[] args) {
		List<String> ips = RpcUtils.getLocalV4IPs();
		String chooseIP = RpcUtils.chooseIP(ips);
		for(String ip:ips){
			System.out.print(ip);
			System.out.println(",");
		}
		System.out.println();
		System.out.println(chooseIP);
		
	}

}
