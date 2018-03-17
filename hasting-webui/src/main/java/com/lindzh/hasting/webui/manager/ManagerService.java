package com.lindzh.hasting.webui.manager;

import com.lindzh.hasting.rpc.RpcService;
import com.lindzh.hasting.rpc.cluster1.ConsumeRpcObject;
import com.lindzh.hasting.rpc.cluster1.HostWeight;
import com.lindzh.hasting.cluster.JSONUtils;
import com.lindzh.hasting.rpc.cluster1.RpcHostAndPort;
import com.lindzh.hasting.cluster.admin.RpcAdminService;
import com.lindzh.hasting.cluster.limit.LimitDefine;
import com.lindzh.hasting.webui.pojo.AppInfo;
import com.lindzh.hasting.webui.pojo.HostInfo;
import com.lindzh.hasting.webui.pojo.LimitInfo;
import com.lindzh.hasting.webui.pojo.ServiceInfo;
import com.lindzh.hasting.webui.utils.Const;
import com.lindzh.hasting.webui.utils.DTOUtils;
import com.lindzh.hasting.webui.biz.*;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by lin on 2016/12/16.
 */
@Service("managerService")
public class ManagerService {

    @Resource
    private RpcAdminService adminService;

    @Resource
    private AppService appService;

    @Resource
    private HostService hostService;

    @Resource
    private ServiceInfoService serviceInfoService;

    @Resource
    private ConsumerService consumerService;

    @Resource
    private ProviderService providerService;

    @Resource
    private LimitService limitService;

    private Logger logger = Logger.getLogger("MANAGER");


    /**
     * 定时任务执行
     */
    public void doFetch(){
        //拿到机器列表
        this.doUpdateServers();
        //拿到服务信息
        this.doFetchHostInfoTask();
        //消费者同步
        this.doSyncConsumers();
        //服务状态和数量
        this.doUpdateServiceCountAndStatus();
        //权重
        this.doWeightTask();

    }

    public void doFetchHostInfoTask(){
        List<HostInfo> hosts = hostService.getProviderOnHosts();
        for(HostInfo host:hosts){
            this.doFetchNewServerInfos(host);
        }
    }

    /**
     * 机器上的服务task
     * @param host
     */
    public void doFetchNewServerInfos(HostInfo host){
        RpcHostAndPort hostAndPort = new RpcHostAndPort();
        hostAndPort.setHost(host.getHost());
        hostAndPort.setPort(host.getPort());
        hostAndPort.setWeight((int)host.getWeight());
        hostAndPort.setToken(host.getToken());
        List<RpcService> rpcServices = adminService.getRpcServices(hostAndPort);

        if(rpcServices!=null&&rpcServices.size()>0){

//            String application = rpcServices.get(0).getApplication();

            /**
             * 机器提供的服务添加
             */
            this.doUpdateProviderServices(rpcServices, host.getAppId(), host.getId());
        }
    }

    /**
     * 机器task
     */
    public void doUpdateServers(){
        List<RpcHostAndPort> rpcServers = adminService.getRpcServers();
        logger.info("[SERVERS] rpcservers:"+JSONUtils.toJSON(rpcServers));
        hostService.updateServersOff();
        hostService.updateServerOn(rpcServers);
        List<HostInfo> offServers = hostService.getOffServers();
        this.doOffServers(offServers);
    }

    /**
     * 消费者task
     */
    public void doSyncConsumers(){
        List<AppInfo> appList = appService.getAppList();
        for(AppInfo app:appList){
            List<ServiceInfo> services = serviceInfoService.getListByAppId(app.getId());
            this.fetchConsumers(services,app.getId());
        }
    }

    /**
     * 更新服务提供者列表
     * @param services
     * @param appId
     * @param hostId
     */
    public List<ServiceInfo> doUpdateProviderServices(List<RpcService> services, long appId, long hostId){
        //先清除,再添加
        providerService.clearServices(appId,hostId);
        return providerService.addOrUpdate(services, appId, hostId);
    }

    /**
     * 获取权重列表
     * @param appInfo
     */
    public void doFetchHostWeights(AppInfo appInfo){
        //权重没必要清除旧的,统一更新
        List<HostWeight> weights = adminService.getWeights(appInfo.getName());
        for(HostWeight hw:weights){
            hostService.getOrAdd(hw,appInfo.getId());
        }
    }

    /**
     * 获取消费者列表
     * @param services
     * @param appId
     */
    public void fetchConsumers(List<ServiceInfo> services,long appId){
        //先清除老的,再添加新的。避免重复和状态不对的
        for(ServiceInfo info:services){
            consumerService.clearConsumers(appId, info.getId());
            List<ConsumeRpcObject> consumers = adminService.getConsumers(info.getGroup(), info.getName(), info.getVersion());
            logger.info("[CONSUMERS] service:"+info.getGroup()+"_"+info.getName()+"_"+info.getVersion()+" consumers:"+JSONUtils.toJSON(consumers));
            for(ConsumeRpcObject consumer:consumers){
                consumerService.addOrUpdate(consumer,appId,info.getId());
            }
        }
    }

    /**
     * 消费者消费状态,更新,服务提供者服务更新
     * @param offServers
     */
    public void doOffServers(List<HostInfo> offServers){
        for(HostInfo server:offServers){
            logger.info("[OFFSERVER] off server:"+ JSONUtils.toJSON(server));
            consumerService.clearConsumers(server.getAppId(),server.getId());
            providerService.clearServices(server.getAppId(),server.getId());
        }
        //更新服务消费者,提供者数量
        this.doUpdateServiceCountAndStatus();
    }

    /**
     * 服务状态,提供者,消费者task
     */
    public void doUpdateServiceCountAndStatus(){
        //更新服务的提供者数量,消费者数量,以及状态
        serviceInfoService.updateProviderCount();
        serviceInfoService.updateServiceStatus();
        serviceInfoService.updateConsumerCount();
    }

    /**
     * 权重task
     */
    public void doWeightTask(){
        updateWeights();
        syncWeights();
    }

    /**
     * 机器权重更新
     */
    public void updateWeights(){
        List<AppInfo> list = appService.getAppList();
        for(AppInfo app:list){
            this.doFetchHostWeights(app);
        }


    }

    /**
     * 同步权重
     */
    public void syncWeights(){
        List<HostInfo> needSyncList = hostService.getNeedSyncList();
        for(HostInfo host:needSyncList){
            AppInfo info = appService.getById(host.getId());
            HostWeight weight = new HostWeight();
            weight.setHost(host.getHost());
            weight.setPort(host.getPort());
            weight.setWeight((int)host.getWantWeight());
            adminService.setWeight(info.getName(),weight);
        }
    }

    /**
     * 限流信息同步
      */
    public void syncLimits(){
        List<AppInfo> needSyncApps = appService.getListByLimitSyncStatus(Const.APP_LIMIT_SYNCED_NO);
        for(AppInfo app:needSyncApps){
            try {
                List<LimitInfo> limits = limitService.getListByAppId(app.getId(), 1000, 0, true);
                List<LimitDefine> limitDefines = DTOUtils.parse(limits);
                adminService.setLimits(app.getName(), limitDefines);

                app.setLimitSyncStatus(Const.APP_LIMIT_SYNCED);
                app.setLimitCount(limits.size());
                app.setLimitSyncTime(System.currentTimeMillis());
                appService.updateApp(app);
            }catch (Exception e){
                logger.error("[LIMIT] sync app limit failed app:"+app.getName(),e);
            }
        }
    }
}
