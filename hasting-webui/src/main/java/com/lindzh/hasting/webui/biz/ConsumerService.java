package com.lindzh.hasting.webui.biz;

import com.lindzh.hasting.rpc.cluster1.ConsumeRpcObject;
import com.lindzh.hasting.webui.dao.ServiceConsumerInfoDao;
import com.lindzh.hasting.webui.pojo.AppInfo;
import com.lindzh.hasting.webui.pojo.HostInfo;
import com.lindzh.hasting.webui.pojo.ServiceConsumerInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by lin on 2016/12/16.
 */
@Service
public class ConsumerService {


    @Resource
    private ServiceConsumerInfoDao serviceConsumerInfoDao;

    @Resource
    private AppService appService;

    @Resource
    private HostService hostService;



    /**
     * 给app和service添加消费者
     * @param consumer
     * @param appId
     * @param serviceId
     */
    public void addOrUpdate(ConsumeRpcObject consumer,long appId,long serviceId){

        AppInfo consumerApp = appService.getOrAddApp(consumer.getApplication());
        if(consumerApp==null){
            return;
        }

        HostInfo host = hostService.getOrAddHost(consumerApp.getId(),consumer.getIp(),true);
        if(host==null){
            return;
        }

        ServiceConsumerInfo consumerInfo = serviceConsumerInfoDao.getConsumer(appId, serviceId, consumerApp.getId(), host.getId());

        if(consumerInfo!=null){
            return;
        }else{
            ServiceConsumerInfo info = new ServiceConsumerInfo();
            info.setTime(System.currentTimeMillis());
            info.setComsumerHostId(host.getId());
            info.setConsumerAppId(consumerApp.getId());
            info.setServiceAppId(appId);
            info.setServiceId(serviceId);
            serviceConsumerInfoDao.addServiceConsumerInfo(info);
        }
    }

    /**
     * 消费者所属应用,消费者host
     * @param appId
     * @param hostId
     */
    public void clearConsumers(long appId,long hostId){
        serviceConsumerInfoDao.deleteByConsumerAppIdAndHostId(appId, hostId);
    }


    public int getServiceConsumeCount(long providerAppId,long providerServiceId){
        return (int)serviceConsumerInfoDao.getServiceConsumeCount(providerAppId, providerServiceId);
    }

}
