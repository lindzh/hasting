package com.lindzh.hasting.cluster.serialize;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.lindzh.hasting.cluster.TestBean;
import com.lindzh.hasting.cluster.serializer.ProtostuffSerializer;
import com.lindzh.hasting.rpc.serializer.JdkSerializer;

public class ProtostuffTest {
	
	public static void benchTest() {
		TestBean testBean = new TestBean();
		testBean.setLimit(4);
		testBean.setMessage("ggggggggggggggggggggggggggggggggggggggggggggg");
		testBean.setOffset(43432);
		testBean.setOrder("645gdfghdfghdf");
		
		long a = 1234;
		boolean ff = false;
		
		ArrayList<String> strs = new ArrayList<String>();
		strs.add("haha");
		strs.add("124545");
		strs.add("this is a simple test");
		
		Object[] arr = new Object[]{a,testBean,ff,strs};
		ProtostuffSerializer serializer = new ProtostuffSerializer();
		byte[] serialize = serializer.serialize(arr);
		int i=0;
		long start = System.currentTimeMillis();
		while(i<10000){
			serializer.serialize(arr);
			i++;
		}
		long end = System.currentTimeMillis();
		Object object = serializer.deserialize(serialize);
		System.out.println("proto:"+serialize.length+" cost:"+(end-start));
		
		JdkSerializer jdkSerializer = new JdkSerializer();
		byte[] bs = jdkSerializer.serialize(arr);
		i=0;
		start = System.currentTimeMillis();
		while(i<10000){
			jdkSerializer.serialize(arr);
			i++;
		}
		end = System.currentTimeMillis();
		System.out.println("jdk:"+bs.length+" cost:"+(end-start));
	}
	
	public static void writeTest(String file) throws IOException {
		TestBean testBean = new TestBean();
		testBean.setLimit(4);
		testBean.setMessage("ggggggggggggggggggggggggggggggggggggggggggggg");
		testBean.setOffset(43432);
		testBean.setOrder("645gdfghdfghdf");
		
		long a = 1234;
		boolean ff = false;
		
		ArrayList<String> strs = new ArrayList<String>();
		strs.add("haha");
		strs.add("124545");
		strs.add("this is a simple test");
		
		Object[] arr = new Object[]{a,testBean,ff,strs};
		ProtostuffSerializer serializer = new ProtostuffSerializer();
		byte[] serialize = serializer.serialize(arr);
		
		FileOutputStream fos = new FileOutputStream(new File(file));
		fos.write(serialize);
		fos.close();
		System.out.println("write finish:"+serialize.length);
	}
	
	public static void main(String[] args) throws IOException {
		String file = "d:\\protostuff.test";
//		writeTest(file);
		readTest(file);
	}
	
	public static void readTest(String file) throws IOException{
		FileInputStream fis = new FileInputStream(new File(file));
		byte[] buf = new byte[204];
		fis.read(buf);
		fis.close();
		ProtostuffSerializer serializer = new ProtostuffSerializer();
		Object object = serializer.deserialize(buf);
		System.out.println(object);
	}

}
