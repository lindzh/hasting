package com.lindzh.hasting.cluster.serialize;

import com.lindzh.hasting.cluster.JSONUtils;
import com.lindzh.hasting.cluster.TestBean;
import com.lindzh.hasting.cluster.serializer.HessianSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by lin on 2016/12/1.
 */
public class HessianTest {

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
        HessianSerializer serializer = new HessianSerializer();
        byte[] serialize = serializer.serialize(arr);
        int i=0;
        long start = System.currentTimeMillis();
        while(i<10000){
            serializer.serialize(arr);
            i++;
        }
        long end = System.currentTimeMillis();
        Object object = serializer.deserialize(serialize);
        System.out.println(JSONUtils.toJSON(object));
        System.out.println("proto:"+serialize.length+" cost:"+(end-start));

        HessianSerializer jdkSerializer = new HessianSerializer();
        byte[] bs = jdkSerializer.serialize(arr);
        i=0;
        start = System.currentTimeMillis();
//        while(i<10000){
//            jdkSerializer.serialize(arr);
//            i++;
//        }
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
        HessianSerializer serializer = new HessianSerializer();
        byte[] serialize = serializer.serialize(arr);

        FileOutputStream fos = new FileOutputStream(new File(file));
        fos.write(serialize);
        fos.close();
        System.out.println("write finish:"+serialize.length);
    }

    public static void main(String[] args) throws IOException {
        String file = "d:\\protostuff.test";
        benchTest();
//		writeTest(file);
//        readTest(file);
    }

    public static void readTest(String file) throws IOException{
        FileInputStream fis = new FileInputStream(new File(file));
        byte[] buf = new byte[204];
        fis.read(buf);
        fis.close();
        HessianSerializer serializer = new HessianSerializer();
        Object object = serializer.deserialize(buf);
        System.out.println(object);
    }

}
