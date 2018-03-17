package com.lindzh.hasting.webui.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by lin on 2016/12/24.
 */
public class CollectionUtils {


        private static Logger logger = Logger.getLogger(CollectionUtils.class);

        public static <T> List<T> collect(List list, String field, Class<T> clazz){
            ArrayList<T> result = new ArrayList<T>();
            if(list!=null&&list.size()>0){
                try {
                    Field f = list.get(0).getClass().getDeclaredField(field);
                    f.setAccessible(true);
                    for(Object obj:list){
                        T r = (T)f.get(obj);
                        result.add(r);
                    }
                } catch (NoSuchFieldException e) {
                    throw new BizException(e);
                } catch (SecurityException e) {
                    throw new BizException(e);
                } catch (IllegalArgumentException e) {
                    throw new BizException(e);
                } catch (IllegalAccessException e) {
                    throw new BizException(e);
                }
            }
            return result;
        }

    public static <K,V> Map<K,V> toMap(List<V> list,String field,Class<K> clazz){
        HashMap<K,V> map = new HashMap<K,V>();
        if(list!=null&&list.size()>0){
            try {
                Field f = list.get(0).getClass().getDeclaredField(field);
                f.setAccessible(true);
                for(Object obj:list){
                    K key = (K)f.get(obj);
                    map.put(key, (V)obj);
                }
            } catch (NoSuchFieldException e) {
                throw new BizException(e);
            } catch (SecurityException e) {
                throw new BizException(e);
            } catch (IllegalArgumentException e) {
                throw new BizException(e);
            } catch (IllegalAccessException e) {
                throw new BizException(e);
            }
        }
        return map;
    }

        public static <T> List<T> collectDistinct(List list,String field,Class<T> clazz){
            ArrayList<T> result = new ArrayList<T>();
            HashSet<T> hashSet = new HashSet<T>();
            if(list!=null&&list.size()>0){
                try {
                    Field f = list.get(0).getClass().getDeclaredField(field);
                    f.setAccessible(true);
                    for(Object obj:list){
                        if(obj!=null){
                            T r = (T)f.get(obj);
                            if(r!=null){
                                hashSet.add(r);
                            }
                        }
                    }
                } catch (NoSuchFieldException e) {
                    throw new BizException(e);
                } catch (SecurityException e) {
                    throw new BizException(e);
                } catch (IllegalArgumentException e) {
                    throw new BizException(e);
                } catch (IllegalAccessException e) {
                    throw new BizException(e);
                }
            }
            result.addAll(hashSet);
            return result;
        }


        public static <K,V> Map<K,List<V>> collectGroup(List<V> list,String field,Class<K> clazz){
            HashMap<K,List<V>> map = new HashMap<K,List<V>>();
            if(list!=null&&list.size()>0){
                try {
                    Field f = list.get(0).getClass().getDeclaredField(field);
                    f.setAccessible(true);
                    for(Object obj:list){
                        K key = (K)f.get(obj);
                        List<V> ll = map.get(key);
                        if(ll!=null){
                            ll.add((V)obj);
                        }else{
                            ll = new ArrayList<V>();
                            ll.add((V)obj);
                            map.put(key, ll);
                        }
                    }
                } catch (NoSuchFieldException e) {
                    throw new BizException(e);
                } catch (SecurityException e) {
                    throw new BizException(e);
                } catch (IllegalArgumentException e) {
                    throw new BizException(e);
                } catch (IllegalAccessException e) {
                    throw new BizException(e);
                }
            }

            return map;
        }

        public static String list2String(List<String> list, String split){
            StringBuilder sBuilder = new StringBuilder();
            for(String string : list){
                sBuilder.append(string);
                sBuilder.append(",");
            }
            String temp = sBuilder.toString();
            return temp.substring(0, temp.length() - 1);
        }

        public static List<String> string2list(String str, String split){
            if(StringUtils.isEmpty(str)){
                return null;
            }
            String[] array = str.split(split);
            return Arrays.asList(array);
        }

}
