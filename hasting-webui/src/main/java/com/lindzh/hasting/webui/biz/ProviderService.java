package com.lindzh.hasting.webui.biz;

import com.lindzh.hasting.rpc.RpcService;
import com.lindzh.hasting.webui.dao.ServiceProviderInfoDao;
import com.lindzh.hasting.webui.pojo.ServiceInfo;
import com.lindzh.hasting.webui.pojo.ServiceProviderInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by lin on 2016/12/16.
 */
@Service
public class ProviderService {

    @Resource
    private ServiceInfoService serviceInfoService;

    @Resource
    private ServiceProviderInfoDao serviceProviderInfoDao;

    public List<ServiceInfo> addOrUpdate(List<RpcService> service, long appId, long hostId){
        List<ServiceInfo> infos = serviceInfoService.addOrupdateService(service, appId);
        for(ServiceInfo info:infos){
            ServiceProviderInfo providerInfo = serviceProviderInfoDao.getByAppHostAndServiceId(appId, hostId, info.getId());
            if(providerInfo==null){
                ServiceProviderInfo providerInfo1 = new ServiceProviderInfo();
                providerInfo1.setServiceId(info.getId());
                providerInfo1.setTime(System.currentTimeMillis());
                providerInfo1.setAppId(appId);
                providerInfo1.setHostId(hostId);
                serviceProviderInfoDao.addServiceProviderInfo(providerInfo1);
            }
        }
        return infos;
    }

    public void clearServices(long appId,long hostId){
        serviceProviderInfoDao.deleteByAppIdAndHostId(appId,hostId);
    }

    public int getServiceProviderCount(long appId,long ServiceId){
        return serviceProviderInfoDao.getServiceProviderCount(appId, ServiceId);
    }
}
