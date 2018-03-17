package com.lindzh.hasting.rpc.generic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GenericParserTest {
	
	public static void main(String[] as) {
		SimpleArgsParser parser = new SimpleArgsParser();
		String[] types = new String[]{"int","int[]","java.util.List<TestBean>"};
		HashMap<String,Object> map = new HashMap<String,Object>();
		map.put("limit", 111);
		map.put("offset", 322);
		map.put("order", "trtr");
		map.put("message", "this is a test");
		ArrayList<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		list.add(map);
		list.add(map);
		Object[] args = new Object[]{222,new int[]{44,3333},list};
		Object[] args2 = parser.parseArgs(types, args);
		System.out.println(args2);
	}

}
