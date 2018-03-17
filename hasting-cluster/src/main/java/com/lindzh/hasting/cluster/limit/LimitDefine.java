package com.lindzh.hasting.cluster.limit;

/**
 * Created by lin on 2017/1/24.
 */
public class LimitDefine {

    /**
     * 限流类型
     */
    private int type;

    /**
     * 应用
     */
    private String application;

    /**
     * 限流服务
     */
    private String service;

    /**
     * 限流方法
     */
    private String method;

    /**
     * 限流时长 单位ms
     */
    private int ttl;

    /**
     * 访问量
     */
    private int count;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }
}
