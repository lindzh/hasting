package com.lindzh.hasting.cluster;

/**
 * 
 * @author lindezhi
 * 集群对象key
 */
public interface RpcClusterConst {
	
	public static final String RPC_REDIS_CHANNEL = "rpc_cluster_notify_channel";

	public static final String RPC_REDIS_HOSTS_KEY = "rpc_cluster_hosts";
	
	public static final String RPC_REDIS_SERVER_SERVICE_PREFIX = "rpc_cluster_node_";
	
	public static final int RPC_REDIS_TTL = 10000;
	
	public static final int CODE_SERVER_START = 100;
	
	public static final int CODE_SERVER_STOP = 200;
	
	public static final int CODE_SERVER_ADD_RPC = 300;
	
	public static final int CODE_SERVER_HEART = 400;
	
}
