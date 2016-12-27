package com.linda.framework.rpc.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lin on 2016/12/27.
 */
public class XAliasUtils {

    public static ConcurrentHashMap<String,ConcurrentHashMap<String,String>> cache = new ConcurrentHashMap<String,ConcurrentHashMap<String,String>>();

    public static void addAlias(Class clazz){

    }

    public static String getFieldAlias(String classAlias,String fieldAlias){

        return null;
    }

    public static String getClassNameByAlias(String classAlias){

        return null;
    }


}
