package com.lindzh.hasting.cluster.etcd;

/**
 * Created by Administrator on 2017/2/15.
 */
public class EtcdUtils {

    /**
     * 限流配置
     * @return
     */
    public static String genLimitKey(String namespace,String application){
        return "/"+namespace+"/limit/" + application;
    }

}
