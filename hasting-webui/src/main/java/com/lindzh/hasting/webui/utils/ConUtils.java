package com.lindzh.hasting.webui.utils;

import com.lindzh.hasting.rpc.RpcService;
import com.lindzh.hasting.rpc.cluster1.HostWeight;
import com.lindzh.hasting.rpc.cluster1.RpcHostAndPort;
import com.lindzh.hasting.webui.pojo.AppInfo;
import com.lindzh.hasting.webui.pojo.HostInfo;
import com.lindzh.hasting.webui.pojo.ServiceInfo;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by lin on 2016/12/16.
 */
public class ConUtils {

    public static AppInfo buildApp(String name){
        AppInfo info = new AppInfo();
        info.setName(name);
        return info;
    }

    public static ServiceInfo convertService(RpcService service, long appId){
        ServiceInfo info = new ServiceInfo();
        info.setName(service.getName());
        info.setGroup(service.getGroup());
        info.setVersion(service.getVersion());
        info.setImpl(service.getImpl());
        info.setAppId(appId);
        return info;
    }

    public static HostInfo buildHostInfo(RpcHostAndPort hostAndPort,long appId){
        HostInfo info = new HostInfo();
        info.setAppId(appId);
        info.setHost(hostAndPort.getHost());
        info.setPort(hostAndPort.getPort());
        info.setTime(System.currentTimeMillis());
        info.setStatus(Const.HOST_STATUS_OFF);
        info.setToken(hostAndPort.getToken());
        info.setWantWeight(hostAndPort.getWeight());
        info.setWeight(hostAndPort.getWeight());
        return info;
    }

    public static HostInfo buildHostInfo(HostWeight hw,long appId){
        HostInfo info = new HostInfo();
        info.setAppId(appId);
        info.setHost(hw.getHost());
        info.setPort(hw.getPort());
        info.setTime(System.currentTimeMillis());
        info.setToken("null");
        info.setStatus(Const.HOST_STATUS_OFF);
        info.setWantWeight(hw.getWeight());
        info.setWeight(hw.getWeight());
        return info;
    }

    public static String fixKeyword(String keyword){
        String mykey = null;
        if(StringUtils.isNotBlank(keyword)){
            mykey = "%"+keyword+"%";
        }
        return mykey;
    }


}
