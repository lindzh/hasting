package com.lindzh.hasting.cluster.hash;

import com.lindzh.hasting.rpc.cluster1.RpcHostAndPort;
import com.lindzh.hasting.rpc.exception.RpcException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 
 * @author lindezhi
 * 随机hashing
 */
public class RoundRobinHashing extends Hashing{

	private AtomicLong sequence = new AtomicLong();

	private AtomicLong currentSequence = new AtomicLong();

	/**
	 * 轮询获取,均匀
	 * @param servers
	 * @return
     */
	@Override
	public String doHash(List<RpcHostAndPort> servers) {
		int max = 0;
		for(RpcHostAndPort server:servers){
			max = max>server.getWeight()?max:server.getWeight();
		}

		long current = currentSequence.getAndIncrement();

		int rand = (int)(current%max);

		ArrayList<RpcHostAndPort> selectedServers = new ArrayList<RpcHostAndPort>();

		for(RpcHostAndPort server:servers){
			if(server.getWeight()>=rand){
				selectedServers.add(server);
			}
		}

		if(selectedServers.size()<1){
			throw new RpcException("round robin hash server no provider for the request");
		}

		if(selectedServers.size()==1){
			return selectedServers.get(0).toString();
		}

		long seqIndex = sequence.getAndIncrement();
		int index = (int)(seqIndex%selectedServers.size());

		return selectedServers.get(index).toString();
	}

}
