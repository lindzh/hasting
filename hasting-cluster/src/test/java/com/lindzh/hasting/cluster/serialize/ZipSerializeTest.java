package com.lindzh.hasting.cluster.serialize;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPOutputStream;

import com.lindzh.hasting.cluster.TestBean;
import com.lindzh.hasting.rpc.serializer.JdkSerializer;

public class ZipSerializeTest {
	
	public static void main(String[] args) throws IOException {
		TestBean testBean = new TestBean();
		testBean.setLimit(4);
		testBean.setMessage("ggggggggggggggggggggggggggggggggggggggggggggg");
		testBean.setOffset(43432);
		testBean.setOrder("645gdfghdfghdf");
		
		JdkSerializer serializer = new JdkSerializer();
		byte[] bs = serializer.serialize(testBean);
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		GZIPOutputStream zos = new GZIPOutputStream(bos);
		
		ObjectOutputStream oos = new ObjectOutputStream(zos);
		
		oos.writeObject(testBean);
		
		oos.close();
		
//		zis.close();
//		bos.close();
		
		byte[] os = bos.toByteArray();
		System.out.println(bs.length+" dest---:"+os.length);
		//187 dest---:161
		
	}

}
