package com.lindzh.hasting.webui.biz;

import com.lindzh.hasting.rpc.RpcService;
import com.lindzh.hasting.webui.dao.ServiceInfoDao;
import com.lindzh.hasting.webui.pojo.AppInfo;
import com.lindzh.hasting.webui.pojo.ServiceInfo;
import com.lindzh.hasting.webui.utils.CollectionUtils;
import com.lindzh.hasting.webui.utils.ConUtils;
import com.lindzh.hasting.webui.utils.Const;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lin on 2016/12/16.
 */
@Service
public class ServiceInfoService {

    @Resource
    private ServiceInfoDao serviceInfoDao;

    @Resource
    private ProviderService providerService;

    @Resource
    private ConsumerService consumerService;

    @Resource
    private AppService appService;

    /**
     * 查询,不存在就添加
     * @param services
     * @param appId
     * @return
     */
    public List<ServiceInfo> addOrupdateService(List<RpcService> services, long appId){
        ArrayList<ServiceInfo> infos = new ArrayList<ServiceInfo>();
        if(services!=null){
            for(RpcService service:services){
                ServiceInfo info = serviceInfoDao.getByAppIdGroupNameVersion(appId, service.getGroup(), service.getName(), service.getVersion());
                if(info==null){
                   info = ConUtils.convertService(service,appId);
                    info.setStatus(Const.SERVICE_OK);
                    info.setProviderCount(1);
                    serviceInfoDao.addServiceInfo(info);
                }
                infos.add(info);
            }
        }
        return infos;
    }

    public void updateProviderCount(){
        serviceInfoDao.updateProviderCount();
    }

    public void updateServiceStatus(){
        serviceInfoDao.updateServiceStatus();
    }

    public void updateConsumerCount(){
        serviceInfoDao.updateConsumerCount();
    }

    /**
     * 获取service列表
     * @param appId
     * @return
     */
    public List<ServiceInfo> getListByAppId(long appId){
        return serviceInfoDao.getListByAppIdAndStatus(appId,Const.SERVICE_ALL,10000,0);
    }

    public long getCountByKeywordAndAppId(String keyword,long appId){
        return serviceInfoDao.getCountByNameAndAppId(ConUtils.fixKeyword(keyword),appId);
    }

    public List<ServiceInfo> getListByKeywordAndAppId(String keyword,long appId,int limit,int offset){
        List<ServiceInfo> infos = serviceInfoDao.getListByNameAndAppId(ConUtils.fixKeyword(keyword), appId, limit, offset);
        this.setApp(infos);
        return infos;
    }

    public ServiceInfo getById(long id,boolean app){
        ServiceInfo info = serviceInfoDao.getById(id);
        if(info!=null&&app){
            AppInfo appInfo = appService.getById(info.getAppId());
            info.setApp(appInfo);
        }
        return info;
    }

    public List<ServiceInfo> setApp(List<ServiceInfo> services){
        if(services!=null&&services.size()>0){
            List<AppInfo> apps = appService.getAppList();
            Map<Long, AppInfo> appMap = CollectionUtils.toMap(apps, "id", Long.class);
            for(ServiceInfo info:services){
                info.setApp(appMap.get(info.getAppId()));
            }
        }
        return services;
    }


    public List<ServiceInfo> getConsumeServicesByAppId(long appId){
        return serviceInfoDao.getConsumeServicesByAppId(appId);
    }

    public List<ServiceInfo> getProvideServicesByHostId(long hostid){
        return serviceInfoDao.getProvideServicesByHostId(hostid);
    }

    public List<ServiceInfo> getConsumeServicesByHostId(long hostid){
        return serviceInfoDao.getConsumeServicesByHostId(hostid);
    }

    public List<ServiceInfo> getListByProviderAndConsumerApp(long providerAppId,long consumeAppId){
        return serviceInfoDao.getListByProviderAndConsumerApp(providerAppId, consumeAppId);
    }

}
