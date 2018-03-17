package com.lindzh.hasting.webui;

import java.util.ArrayList;

import com.lindzh.hasting.cluster.JSONUtils;
import com.lindzh.hasting.webui.service.RpcConfig;

public class RpcConfigGen {
	
	public static void main(String[] args) {
		ArrayList<RpcConfig> configs = new ArrayList<RpcConfig>();
		configs.add(new RpcConfig());
		System.out.println(JSONUtils.toJSON(configs));
	}

}
