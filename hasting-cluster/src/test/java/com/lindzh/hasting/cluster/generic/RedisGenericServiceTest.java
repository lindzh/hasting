package com.lindzh.hasting.cluster.generic;

import java.util.HashMap;

import com.lindzh.hasting.cluster.redis.RedisRpcClient;
import com.lindzh.hasting.rpc.generic.GenericService;
import com.lindzh.hasting.rpc.utils.RpcUtils;
import com.linda.jetcd.JSONUtils;

public class RedisGenericServiceTest {
	
	public static void main(String[] args) {
		
		RedisRpcClient client = new RedisRpcClient();
		client.setRedisHost("192.168.139.129");
		client.setRedisPort(7770);
		client.startService();
		GenericService genericService = client.register(GenericService.class);
		int index = 10000;
		while(true){
			String[] getBeanTypes = new String[]{"TestBean","int"};
			HashMap<String,Object> map = new HashMap<String,Object>();
			map.put("limit", index);
			map.put("offset", index*10);
			map.put("order", "index-"+index);
			map.put("message", "this is a test index+"+index);
			Object[] getBeanArgs = new Object[]{map,index*100+5};
			Object hh = genericService.invoke(null,"HelloRpcService", RpcUtils.DEFAULT_VERSION, "getBean", getBeanTypes, getBeanArgs);
			System.out.println(JSONUtils.toJSON(hh));
			index++;
			try {
				Thread.sleep(3000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}

}
