package com.lindzh.hasting.cluster.etcd;

import com.lindzh.hasting.cluster.JSONUtils;
import com.lindzh.hasting.cluster.limit.LimitConst;
import com.lindzh.hasting.cluster.limit.LimitDefine;
import com.linda.jetcd.EtcdClient;

import java.util.ArrayList;

/**
 * Created by lin on 2016/12/12.
 */
public class Etcdtest {
    public static void main(String[] args) {
        EtcdClient client = new EtcdClient("http://192.168.139.128:2911");

        client.start();

        ArrayList<LimitDefine> limitDefines = new ArrayList<LimitDefine>();
        LimitDefine define = new LimitDefine();
        define.setType(LimitConst.LIMIT_ALL);
        define.setCount(4);
        define.setTtl(7000);
        limitDefines.add(define);

//        client.set("/myapp/weight/simple/weights/172.17.9.251:3351","50");
//
//        client.set("/myapp/weight/simple/node","455555");

        client.set("/myapp/limit/simple", JSONUtils.toJSON(limitDefines));
        client.stop();

    }
}
