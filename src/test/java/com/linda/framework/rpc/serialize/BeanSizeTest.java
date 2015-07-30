package com.linda.framework.rpc.serialize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.linda.framework.rpc.serializer.JdkSerializer;

public class BeanSizeTest {
	
	public static void main(String[] args) {
		
		int idx = 0;
		long now = System.currentTimeMillis();
		
		List<HelloBean> list = new ArrayList<HelloBean>();
		
		while(idx<100){
			HelloBean bean = new HelloBean();
			bean.setAddTime(now+idx);
			bean.setAddUserId(123+idx);
			bean.setAge(idx%60+5);
			bean.setBelongs(1232+idx);
			bean.setCategory("this ia fefebvegioewhgoiewhruwqhrfiuew"+idx);
			bean.setCity("杭州市"+idx);
			bean.setClassic("this is classic test haha"+idx);
			bean.setCountry("中国"+idx);
			bean.setDesc("哈哈哈哈哈 O(∩_∩O(∩_∩)O哈！"+idx);
			bean.setEstimate("my estimate is hahahha"+idx);
			bean.setFirstName("linda test"+idx);
			bean.setFullName("full name test "+idx);
			
			bean.setId(54354354353L);
			bean.setLastName("my last name "+idx);
			bean.setLastPrice(54.31232);
			bean.setLat(123);
			bean.setLnt(432);
			bean.setName("this is name "+idx);
			bean.setPrice(43.32f);
			bean.setProvice("浙江省");
			idx++;
			list.add(bean);
		}
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("code", 200);
		map.put("data", list);
		map.put("msg", "success");
		JdkSerializer serializer = new JdkSerializer();
		byte[] bs = serializer.serialize(map);
		System.out.println(bs.length);
	}

}
