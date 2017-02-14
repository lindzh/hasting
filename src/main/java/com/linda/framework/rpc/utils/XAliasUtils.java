package com.linda.framework.rpc.utils;

import com.linda.framework.rpc.exception.RpcException;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lin on 2016/12/27.
 */
public class XAliasUtils {

    public static ConcurrentHashMap<String,ConcurrentHashMap<String,String>> fieldAliasCache = new ConcurrentHashMap<String,ConcurrentHashMap<String,String>>();

    public static ConcurrentHashMap<String,String> classAliasCache = new ConcurrentHashMap<String,String>();

    public static ConcurrentHashMap<String,String> aliasClassCache = new ConcurrentHashMap<String,String>();

    static {
        addAlias("int","int");
        addAlias("short","short");
        addAlias("long","long");
        addAlias("byte","byte");
        addAlias("float","float");
        addAlias("double","double");
        addAlias("boolean","boolean");
        addAlias("char","char");

        addAlias("string","java.lang.String");
        addAlias("String","java.lang.String");

        addAlias("Integer","java.lang.Integer");
        addAlias("Short","java.lang.Short");
        addAlias("Long","java.lang.Long");
        addAlias("Byte","java.lang.Byte");
        addAlias("Float","java.lang.Float");
        addAlias("Double","java.lang.Double");
        addAlias("Boolean","java.lang.Boolean");
        addAlias("Character","java.lang.Character");

        addAlias("List","java.util.List");
        addAlias("Map","java.util.Map");
        addAlias("Set","java.util.Set");

    }

    public static boolean isInnerType(Class clazz){
        return aliasClassCache.get(clazz.getName())!=null;
    }

    public static void addAlias(Class clazz){
        if(clazz==Object.class){
            return ;
        }

        if(clazz.isPrimitive()){
            return;
        }
        if(clazz.isEnum()){
            throw new RpcException("not supported enum for simple serializer "+clazz);
        }
        if(clazz.isArray()){
            addAlias(clazz.getComponentType());
            return;
        }

        if(List.class.isAssignableFrom(clazz)){
            Type genType = clazz.getGenericSuperclass();
            if (!(genType instanceof ParameterizedType)) {
                return ;
            }
            ParameterizedType pt = (ParameterizedType)genType;
            Type[] actualTypeArguments = pt.getActualTypeArguments();
            addAlias((Class)actualTypeArguments[0]);
            return;
        }

        if(Set.class.isAssignableFrom(clazz)){
            Type genType = clazz.getGenericSuperclass();
            if (!(genType instanceof ParameterizedType)) {
                return ;
            }
            ParameterizedType pt = (ParameterizedType)genType;
            Type[] actualTypeArguments = pt.getActualTypeArguments();
            addAlias((Class)actualTypeArguments[0]);
            return;
        }

        if(Map.class.isAssignableFrom(clazz)){
            Type genType = clazz.getGenericSuperclass();
            if (!(genType instanceof ParameterizedType)) {
                return ;
            }
            ParameterizedType pt = (ParameterizedType)genType;
            Type[] actualTypeArguments = pt.getActualTypeArguments();
            addAlias((Class)actualTypeArguments[0]);
            addAlias((Class)actualTypeArguments[1]);
            return;
        }

        if(isInnerType(clazz)){
            return;
        }

        if(clazz.getCanonicalName().startsWith("java.")||clazz.getCanonicalName().startsWith("javax.")){
            throw new RpcException("not supported java type for simple serializer "+clazz);
        }

        XNamespace xname = (XNamespace)clazz.getAnnotation(XNamespace.class);
        String name = clazz.getCanonicalName();
        if(xname!=null){
            String namespace = xname.value().trim();
            if(namespace.length()>0){
                name = namespace+"."+clazz.getSimpleName();
            }
        }
        if(classAliasCache.get(name)==null){
            classAliasCache.put(name,clazz.getCanonicalName());
            aliasClassCache.put(clazz.getCanonicalName(),name);
        }else{
            if(!classAliasCache.get(name).equals(clazz.getCanonicalName())){
                throw new RpcException("dumplicate alias defination "+clazz+":"+name);
            }
        }

        if(clazz.isInterface()){
            return;
        }

        ConcurrentHashMap<String, String> fieldCache = fieldAliasCache.get(name);
        if(fieldCache==null){
            fieldCache = new ConcurrentHashMap<String,String>();
            fieldAliasCache.put(name,fieldCache);
        }

        Set<Field> classField = getClassField(clazz);
        for(Field field:classField){
            String fname = field.getName();
            XName falias = (XName)field.getAnnotation(XName.class);
            if(falias!=null){
                String fAliasName = falias.value().trim();
                if(fAliasName.length()>0){
                    fname = fAliasName;
                }
            }
            fieldCache.put(fname,field.getName());
            //类型再定义
            addAlias(field.getType());
        }
    }



    public static Set<Field> getClassField(Class clazz){
        HashSet<Field> result = new HashSet<Field>();
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
            result.add(f);
        }

        result.addAll(getClassField(clazz.getSuperclass()));
        return result;
    }

    public static void addAlias(String alias,String className){
        classAliasCache.put(alias,className);
        aliasClassCache.put(className,alias);
    }

    public static String getFieldNameByAlias(String classAlias,String fieldAlias){
        ConcurrentHashMap<String, String> fieldAliasMap = fieldAliasCache.get(classAlias);
        if(fieldAliasMap==null){
            return fieldAlias;
        }
        String alias = fieldAliasMap.get(fieldAlias);
        if(alias==null){
            return fieldAlias;
        }
        return alias;
    }

    public static String getClassNameByAlias(String classAlias){
        String alias = classAliasCache.get(classAlias);
        if(alias==null){
            return classAlias;
        }else{
            return alias;
        }
    }

    public static String getAlias(String className){
        return classAliasCache.get(className);
    }

    /**
     * 给service添加alias,添加序列化alias支持
     * @param clazz
     */
    public static void addServiceRefType(Class clazz){
        Method[] declaredMethods = clazz.getDeclaredMethods();
        for(Method method:declaredMethods){
            Class<?>[] parameterTypes = method.getParameterTypes();
            for(Class<?> clazz1:parameterTypes){
                addAlias(clazz1);
            }
        }
        addAlias(clazz);
    }

}
