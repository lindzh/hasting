package com.lindzh.hasting.cluster.hash;

import com.lindzh.hasting.rpc.cluster1.RpcHostAndPort;
import com.lindzh.hasting.rpc.exception.RpcException;

import java.util.List;
import java.util.Random;

/**
 * 
 * @author lindezhi
 * 随机服务器hash
 */
public class RandomHashing extends Hashing{

	private Random random = new Random();
	
	@Override
	public String doHash(List<RpcHostAndPort> servers) {
		int sum = 0;
		for(RpcHostAndPort server:servers){
			sum += server.getWeight();
		}

		int idx = random.nextInt(sum);

		for(RpcHostAndPort server:servers){
			idx = idx-server.getWeight();
			if(idx<0){
				return server.toString();
			}
		}
		throw new RpcException("no provider use random hash do hash");
	}

}
