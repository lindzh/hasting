package com.lindzh.hasting.webui.biz;

import com.lindzh.hasting.webui.dao.LimitInfoDao;
import com.lindzh.hasting.webui.pojo.AppInfo;
import com.lindzh.hasting.webui.pojo.LimitInfo;
import com.lindzh.hasting.webui.utils.Const;
import com.lindzh.hasting.webui.utils.RpcTransaction;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Administrator on 2017/2/15.
 */
@Service
public class LimitService {

    @Resource
    private AppService appService;

    @Resource
    private LimitInfoDao limitInfoDao;

    public List<LimitInfo> getListByAppId(long appId, int limit, int offset, boolean app){
        List<LimitInfo> list = limitInfoDao.getListByAppId(appId, limit, offset);
        return setLimitAppInfos(list,app);
    }

    public LimitInfo getById(long id){
        return limitInfoDao.getById(id);
    }

    private List<LimitInfo> setLimitAppInfos(List<LimitInfo> list,boolean app){
        if(app){
            for (LimitInfo info : list) {
                AppInfo appInfo = appService.getById(info.getLimitAppId());
                info.setLimitAppInfo(appInfo);
            }
        }
        return list;
    }

    /**
     * 单个更新
     * @param limitInfo
     */
    public void addOrUpdateLimit(LimitInfo limitInfo){
        long now = System.currentTimeMillis();
        limitInfo.setUpdateTime(now);
        if(limitInfo.getId()>0){
            limitInfoDao.updateById(limitInfo);
        }else{
            limitInfoDao.addLimitInfo(limitInfo);
        }
        AppInfo app = appService.getById(limitInfo.getAppId());
        app.setLimitSyncStatus(Const.APP_LIMIT_SYNCED_NO);
        appService.updateApp(app);
    }

    /**
     * 更新限流信息
     * @param list
     * @param app
     */
    @RpcTransaction
    public void updateLimits(List<LimitInfo> list, AppInfo app){
        long now = System.currentTimeMillis();
        for(LimitInfo info:list){
            info.setAppId(app.getId());
            info.setUpdateTime(now);
        }
        limitInfoDao.deleteByAppId(app.getId());
        limitInfoDao.batchAdd(list);
        app.setLimitCount(list.size());
        app.setLimitSyncStatus(Const.APP_LIMIT_SYNCED_NO);
        appService.updateApp(app);
    }

    public void deleteLimit(LimitInfo limitInfo,long appId){
        limitInfoDao.deleteById(limitInfo.getId());
        AppInfo app = appService.getById(limitInfo.getAppId());
        app.setLimitCount(app.getLimitCount()-1);
        app.setLimitSyncStatus(Const.APP_LIMIT_SYNCED_NO);
        appService.updateApp(app);
    }
}
