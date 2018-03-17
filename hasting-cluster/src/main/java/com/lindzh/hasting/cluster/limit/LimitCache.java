package com.lindzh.hasting.cluster.limit;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


/**
 * Created by lin on 2017/1/24.
 */
public class LimitCache {

    /**
     * 指定应用限流
     */
    private ConcurrentHashMap<String,LimitDefine> applicationLimit = new ConcurrentHashMap<String,LimitDefine>();

    /**
     * 内部限流
     */
    private ConcurrentHashMap<String,LimitDefine> innerLimit = new ConcurrentHashMap<String, LimitDefine>();


    private class StatBean{
        private AtomicLong count;
        private long start = 0;

        StatBean(){
            count = new AtomicLong(1);
            start = System.currentTimeMillis();
        }
    }

    /**
     * 限流cache
     */
    private ConcurrentHashMap<String,StatBean> limitCache = new ConcurrentHashMap<String, StatBean>();



    public void addOrUpdate(List<LimitDefine> limits){
        if(limits!=null){
            ConcurrentHashMap<String,LimitDefine> bakinnerLimit = new ConcurrentHashMap<String, LimitDefine>();
            ConcurrentHashMap<String,LimitDefine> bakapplicationLimit = new ConcurrentHashMap<String,LimitDefine>();
            //
            for(LimitDefine limit:limits){
                if(limit.getType()==LimitConst.LIMIT_ALL){
                    bakinnerLimit.put(genKey(LimitConst.SYSTEM_LIMIT,null,null,null),limit);
                }else if(limit.getType()==LimitConst.LIMIT_SERVICE){
                    if(limit.getService()!=null){
                        bakinnerLimit.put(genKey(LimitConst.SYSTEM_LIMIT,null,limit.getService(),null),limit);
                    }
                }else if(limit.getType()==LimitConst.LIMIT_METHOD){
                    if(limit!=null&&limit.getMethod()!=null){
                        bakinnerLimit.put(genKey(LimitConst.SYSTEM_LIMIT,null,limit.getService(),limit.getMethod()),limit);
                    }
                }
                if(limit.getApplication()==null){
                    continue;
                }
                if(limit.getType()==LimitConst.LIMIT_APP_ALL){
                    bakapplicationLimit.put(genKey(LimitConst.OUTER_LIMIT,limit.getApplication(),null,null),limit);
                }else if(limit.getType()==LimitConst.LIMIT_APP_SERVICE){
                    if(limit.getService()!=null){
                        bakapplicationLimit.put(genKey(LimitConst.OUTER_LIMIT,limit.getApplication(),limit.getService(),null),limit);
                    }
                }else if(limit.getType()==LimitConst.LIMIT_APP_METHOD){
                    if(limit.getService()!=null&&limit.getMethod()!=null){
                        bakapplicationLimit.put(genKey(LimitConst.OUTER_LIMIT,limit.getApplication(),limit.getService(),limit.getMethod()),limit);
                    }
                }
            }
            //更新缓存
            innerLimit = bakinnerLimit;
            applicationLimit = bakapplicationLimit;
        }
    }

    private String genKey(String prefix,String application,String service,String method){
        StringBuilder sb = new StringBuilder();
        sb.append(prefix);
        if(application!=null){
            sb.append("_");
            sb.append(application);
        }
        if(service!=null){
            sb.append("_");
            sb.append(service);
        }
        if(service!=null&&method!=null){
            sb.append("_");
            sb.append(method);
        }
        return sb.toString();
    }

    /**
     * 执行限流
     * @param limit
     * @param key
     * @return
     */
    private boolean doLimit(LimitDefine limit,String key){
        long count = 1;
        long now = System.currentTimeMillis();
        StatBean bean = limitCache.get(key);
        if(bean!=null){
            if(now-bean.start>limit.getTtl()){
                bean = new StatBean();
                limitCache.put(key,bean);
            }else{
                count = bean.count.incrementAndGet();
            }
        }else{
            bean = new StatBean();
            limitCache.put(key,bean);
        }
        if(count>limit.getCount()){
            return false;
        }else{
            return true;
        }
    }

    public boolean accept(String application,String service,String method){
        //系统限流定义
        LimitDefine systemLimit = null;
        LimitDefine systemMethod = innerLimit.get(genKey(LimitConst.SYSTEM_LIMIT, null, service, method));
        if(systemMethod==null){
            LimitDefine systemService = innerLimit.get(genKey(LimitConst.SYSTEM_LIMIT, null, service, null));
            if(systemService==null){
                LimitDefine systemAll = innerLimit.get(genKey(LimitConst.SYSTEM_LIMIT, null, null, null));
                systemLimit = systemAll;
            }else{
                systemLimit = systemService;
            }
        }else{
            systemLimit = systemMethod;
        }

        //系统限流
        if(systemLimit!=null){
            boolean accept = doLimit(systemLimit,genKey(LimitConst.SYSTEM_LIMIT,null,service,method));
            if(!accept){
                return false;
            }
        }

        //未知应用不做应用限流
        if(application==null){
            return true;
        }

        //针对应用限流定义
        LimitDefine appLimit = null;
        LimitDefine appMethod = applicationLimit.get(genKey(LimitConst.OUTER_LIMIT, application, service, method));
        if(appMethod==null){
            LimitDefine appService = applicationLimit.get(genKey(LimitConst.OUTER_LIMIT, application, service, null));
            if(appService==null){
                LimitDefine appAll = applicationLimit.get(genKey(LimitConst.OUTER_LIMIT, application, null, null));
                appLimit = appAll;
            }else{
                appLimit = appService;
            }
        }else{
            appLimit = appMethod;
        }

        //应用限流
        if(appLimit!=null){
            boolean accept = doLimit(systemLimit,genKey(LimitConst.OUTER_LIMIT,application,service,method));
            if(!accept){
                return false;
            }
        }

        return true;
    }
}
