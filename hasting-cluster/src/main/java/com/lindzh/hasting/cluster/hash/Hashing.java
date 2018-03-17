package com.lindzh.hasting.cluster.hash;

import com.lindzh.hasting.cluster.JSONUtils;
import com.lindzh.hasting.rpc.cluster1.RpcHostAndPort;
import com.lindzh.hasting.rpc.exception.RpcException;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author lindezhi
 * 集群hashing 服务端列表支持
 * 需求:需要增加权重
 */
public abstract class Hashing {

	/**
	 * 通过权重过滤,权重<0表示禁止访问
	 * @param servers
	 * @return
     */
	protected List<RpcHostAndPort> filterServers(List<RpcHostAndPort> servers){
		ArrayList<RpcHostAndPort> list = new ArrayList<RpcHostAndPort>();

		for(RpcHostAndPort server:servers){
			if(server.getWeight()>0){
				list.add(server);
			}
		}

		return list;
	}


	public abstract String doHash(List<RpcHostAndPort> servers);

	/**
	 * 通过权重过滤
	 * @param servers
	 * @return
     */
	public String hash(List<RpcHostAndPort> servers){
		if(servers!=null){
			System.out.println(JSONUtils.toJSON(servers));
			List<RpcHostAndPort> fServers = this.filterServers(servers);
			if(fServers.size()<1){
				throw new RpcException("no provider use for the request weight limited");
			}
			if(fServers.size()==1){
				return fServers.get(0).toString();
			}
			String result = this.doHash(fServers);
			return result;
		}else{
			throw new RpcException("no provider use for the request,no servers");
		}
	}
	
}
