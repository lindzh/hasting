package com.lindzh.hasting.webui.service;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lindzh.hasting.rpc.exception.RpcException;

@JsonIgnoreProperties(ignoreUnknown=true)
public class RpcConfig {

	private String namespace;

	private String protocol;

	private String etcdUrl;

	private String redisHost;

	private int redisPort;

	private String sentinelMaster;

	private Set<String> sentinels;

	private String providerHost;

	private int providerPort;
	
	private String zkConnectionString;
	
	private String md5;
	
	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getEtcdUrl() {
		return etcdUrl;
	}

	public void setEtcdUrl(String etcdUrl) {
		this.etcdUrl = etcdUrl;
	}

	public String getRedisHost() {
		return redisHost;
	}

	public void setRedisHost(String redisHost) {
		this.redisHost = redisHost;
	}

	public int getRedisPort() {
		return redisPort;
	}

	public void setRedisPort(int redisPort) {
		this.redisPort = redisPort;
	}

	public String getSentinelMaster() {
		return sentinelMaster;
	}

	public void setSentinelMaster(String sentinelMaster) {
		this.sentinelMaster = sentinelMaster;
	}

	public Set<String> getSentinels() {
		return sentinels;
	}

	public void setSentinels(Set<String> sentinels) {
		this.sentinels = sentinels;
	}

	public String getProviderHost() {
		return providerHost;
	}

	public void setProviderHost(String providerHost) {
		this.providerHost = providerHost;
	}

	public int getProviderPort() {
		return providerPort;
	}

	public void setProviderPort(int providerPort) {
		this.providerPort = providerPort;
	}
	

	public String getZkConnectionString() {
		return zkConnectionString;
	}

	public void setZkConnectionString(String zkConnectionString) {
		this.zkConnectionString = zkConnectionString;
	}
	
	public String getInfo(){
		if(protocol.equalsIgnoreCase("etcd")){
			return this.etcdUrl;
		}else if(protocol.equalsIgnoreCase("simple")){
			return this.providerHost+":"+this.providerPort;
		}else if(protocol.equalsIgnoreCase("zookeeper")){
			return this.zkConnectionString;
		}else{
			if(this.redisHost!=null&&this.redisPort>0){
				return this.redisHost+":"+this.redisPort;
			}else{
				return "sentinel master:"+this.sentinelMaster;
			}
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("RpcConfig [namespace=" + namespace + ", protocol=" + protocol + ", etcdUrl=" + etcdUrl + ", redisHost=" + redisHost + ", redisPort="
				+ redisPort + ", sentinelMaster=" + sentinelMaster);
		sb.append(", sentinels=[");
		boolean has = false;
		if(sentinels!=null){
			for(String sentinel:sentinels){
				if(has){
					sb.append(",");
				}
				sb.append(sentinel);
				has = true;
			}
		}
		sb.append("], providerHost=" + providerHost + ", providerPort="
				+ providerPort + ", zkConnectionString=" + zkConnectionString + "]");
		return sb.toString();
	}

	public static enum RpcProtocol {

		SIMPLE("simple"), REDIS("redis"), ETCD("etcd"), ZOOKEEPER("zookeeper");
		private String protocol;

		RpcProtocol(String protocol) {
			this.protocol = protocol;
		}

		public String getProtocol() {
			return protocol;
		}

		public static RpcProtocol getByName(String name) {
			RpcProtocol[] values = RpcProtocol.values();
			for (RpcProtocol v : values) {
				if (v.getProtocol().equalsIgnoreCase(name)) {
					return v;
				}
			}
			throw new RpcException("can't find rpc cluster protocol of " + name);
		}
	}
}
