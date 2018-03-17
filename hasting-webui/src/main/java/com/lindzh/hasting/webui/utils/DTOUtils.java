package com.lindzh.hasting.webui.utils;

import com.lindzh.hasting.cluster.limit.LimitDefine;
import com.lindzh.hasting.webui.pojo.AppInfo;
import com.lindzh.hasting.webui.pojo.HostInfo;
import com.lindzh.hasting.webui.pojo.LimitInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lin on 2016/12/25.
 */
public class DTOUtils {

    public static List<AppInfo> groupByApp(List<HostInfo> hosts){
        for(HostInfo host:hosts){
            host.getApp().getHosts().add(host);
        }
        List<AppInfo> apps = CollectionUtils.collect(hosts, "app", AppInfo.class);
        for(HostInfo host:hosts){
            host.setApp(null);
        }
        return apps;
    }

    /**
     * 转换
     * @param infos
     * @return
     */
    public static List<LimitDefine> parse(List<LimitInfo> infos){
        ArrayList<LimitDefine> limitDefines = new ArrayList<LimitDefine>();
        if(infos!=null){
            for (LimitInfo info : infos) {
                LimitDefine define = new LimitDefine();
                define.setTtl(info.getTtl());
                define.setCount(info.getCount());
                define.setType(info.getType());
                if(info.getLimitAppInfo()!=null){
                    define.setApplication(info.getLimitAppInfo().getName());
                }
                define.setMethod(info.getMethod());
                define.setService(info.getService());
                limitDefines.add(define);
            }
        }
        return limitDefines;
    }
}
