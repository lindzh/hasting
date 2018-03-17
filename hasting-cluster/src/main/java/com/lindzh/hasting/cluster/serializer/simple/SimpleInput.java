package com.lindzh.hasting.cluster.serializer.simple;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Created by lin on 2016/12/2.
 */
public class SimpleInput {

    private byte[] data;

    private ByteArrayInputStream bis;

    private DataInputStream dis;

    public SimpleInput(byte[] data){
        this.data = data;
        this.bis = new ByteArrayInputStream(data);
        this.dis = new DataInputStream(bis);
    }

    private Class getClass(byte type,String name) throws ClassNotFoundException, IOException {
        if(type==SimpleConst.intType){
            return int.class;
        }else if(type==SimpleConst.shortType){
            return short.class;
        }else if(type==SimpleConst.longType){
            return long.class;
        }else if(type==SimpleConst.byteType){
            return byte.class;
        }else if(type==SimpleConst.floatType){
            return float.class;
        }else if(type==SimpleConst.doubleType){
            return double.class;
        }else if(type==SimpleConst.booleanType){
            return boolean.class;
        }else if(type==SimpleConst.charType){
            return char.class;
        }

        else if(type==SimpleConst.IntegerType){
            return Integer.class;
        }else if(type==SimpleConst.ShortType){
            return Short.class;
        }else if(type==SimpleConst.LongType){
            return Long.class;
        }else if(type==SimpleConst.ByteType){
            return Byte.class;
        }else if(type==SimpleConst.BooleanType){
            return Boolean.class;
        }else if(type==SimpleConst.FloatType){
            return Float.class;
        }else if(type==SimpleConst.DoubleType){
            return Double.class;
        }else if(type==SimpleConst.CharacterType){
            return Character.class;
        }

        else if(type==SimpleConst.StringType){
            return String.class;
        }

        //Object[]
        else if(type==SimpleConst.AnyType){
            return Object.class;
        }
        //自定义对象
        else if(type==SimpleConst.ObjectType){
            return Class.forName(name);
        }

        else {
            throw new IOException("unknown type class "+type);
        }

    }

    public Object readObject() throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
       byte type =  dis.readByte();

        //基本类型
        if(type==(SimpleConst.intType|SimpleConst.NotNull)){
           return dis.readInt();
        }else if(type==(SimpleConst.shortType|SimpleConst.NotNull)){
            return dis.readShort();
        }else if(type==(SimpleConst.longType|SimpleConst.NotNull)){
            return dis.readLong();
        }else if(type==(SimpleConst.byteType|SimpleConst.NotNull)){
            return dis.readByte();
        }else if(type==(SimpleConst.booleanType|SimpleConst.NotNull)){
            return dis.readBoolean();
        }else if(type==(SimpleConst.floatType|SimpleConst.NotNull)){
            return dis.readFloat();
        }else if(type==(SimpleConst.doubleType|SimpleConst.NotNull)){
            return dis.readDouble();
        }else if(type==(SimpleConst.charType|SimpleConst.NotNull)){
            return dis.readChar();
        }

        //包装类型
        else if(type==(SimpleConst.IntegerType|SimpleConst.NotNull)){
            return (Integer)dis.readInt();
        }else if(type==(SimpleConst.ShortType|SimpleConst.NotNull)){
            return (Short)dis.readShort();
        }else if(type==(SimpleConst.LongType|SimpleConst.NotNull)){
            return (Long)dis.readLong();
        }else if(type==(SimpleConst.FloatType|SimpleConst.NotNull)){
            return (Float)dis.readFloat();
        }else if(type==(SimpleConst.DoubleType|SimpleConst.NotNull)){
            return (Double)dis.readDouble();
        }else if(type==(SimpleConst.BooleanType|SimpleConst.NotNull)){
            return (Boolean)dis.readBoolean();
        }else if(type==(SimpleConst.ByteType|SimpleConst.NotNull)){
            return (Byte)dis.readByte();
        }else if(type==(SimpleConst.CharacterType|SimpleConst.NotNull)){
            return (Character)dis.readChar();
        }else if(type==(SimpleConst.StringType|SimpleConst.NotNull)){
            return this.readString(false);
        }

        //null
        else if(type==SimpleConst.IntegerType){
            return null;
        }else if(type==SimpleConst.ShortType){
            return null;
        }else if(type==SimpleConst.LongType){
            return null;
        }else if(type==SimpleConst.FloatType){
            return null;
        }else if(type==SimpleConst.DoubleType){
            return null;
        }else if(type==SimpleConst.BooleanType){
            return null;
        }else if(type==SimpleConst.ByteType){
            return null;
        }else if(type==SimpleConst.CharacterType){
            return null;
        }else if(type==SimpleConst.StringType){
            return null;
        }

        //arr
        else if(type==(SimpleConst.ArrayType|SimpleConst.NotNull)){
            short len = dis.readShort();

            //数组里面的item类型
            byte itemType = dis.readByte();
            String clazz = this.readString(true);

            ArrayList list = new ArrayList();
            if(len>0){
                for(int i=0;i<len;i++){
                    list.add(this.readObject());
                }
            }

            //具体类型组合
            Class zz = this.getClass(itemType,clazz);
            if(len>0){
                Object result =  Array.newInstance(zz, len);
                for(int i=0;i<len;i++){
                    Array.set(result,i,list.get(i));
                }
                return result;
            }else{
                return Array.newInstance(zz, 0);
            }
        }
        //set
        else if(type==(SimpleConst.SetType|SimpleConst.NotNull)){
            short len = dis.readShort();
            HashSet list = new HashSet();
            if(len>0){
                for(int i=0;i<len;i++){
                    list.add(this.readObject());
                }
            }
            return list;
        }
        //list
        else if(type==(SimpleConst.ListType|SimpleConst.NotNull)){
            short len = dis.readShort();
            ArrayList list = new ArrayList();
            if(len>0){
                for(int i=0;i<len;i++){
                    list.add(this.readObject());
                }
            }
            return list;
        }
        //map
        else if(type==(SimpleConst.MapType|SimpleConst.NotNull)){
            short len = dis.readShort();
            HashMap list = new HashMap();
            if(len>0){
                for(int i=0;i<len;i++){
                    Object key = this.readObject();
                    Object value = this.readObject();
                    list.put(key,value);
                }
            }
            return list;
        }

        else if(type==SimpleConst.ArrayType){
            return null;
        }else if(type==SimpleConst.SetType){
            return null;
        }else if(type==SimpleConst.ListType){
            return null;
        }else if(type==SimpleConst.MapType){
            return null;
        }

        //object
        else if(type==(SimpleConst.ObjectType|SimpleConst.NotNull)){
            String clazzName = this.readString(true);
            Class clazz = Class.forName(clazzName);
            Object obj = clazz.newInstance();
            Map<String,Field> fmap = this.getClassField(clazz);
            short flen = dis.readShort();
            for(int i=0;i<flen;i++){
               String fname = this.readString(true);
                Object fvalue = this.readObject();
                Field ff = fmap.get(fname);
                ff.set(obj,fvalue);
            }
            return obj;
        }
        //null object
        else if(type==SimpleConst.ObjectType){
            return null;
        }else{
            throw new IOException("not supported type:"+type);
        }
    }

    private String readString(boolean readType) throws IOException {
        if(readType){
            byte strType = dis.readByte();
            if(strType!=(SimpleConst.StringType|SimpleConst.NotNull)){
                throw new IOException("read string type wrong "+strType);
            }
        }
        short len = dis.readShort();
        byte[] data = new byte[len];
        dis.read(data);
        return new String(data,"utf-8");
    }

    private Map<String,Field> getClassField(Class clazz){
        HashMap<String,Field> result = new HashMap<String,Field>();
        if(clazz==Object.class){
            return result;
        }

        Field[] fields = clazz.getDeclaredFields();

        for(Field f:fields){
            int m = f.getModifiers();
            if(Modifier.isFinal(m)){
                continue;
            }
            if(Modifier.isStatic(m)){
                continue;
            }

            f.setAccessible(true);
            result.put(f.getName(),f);
        }

        result.putAll(this.getClassField(clazz.getSuperclass()));
        return result;
    }
}
