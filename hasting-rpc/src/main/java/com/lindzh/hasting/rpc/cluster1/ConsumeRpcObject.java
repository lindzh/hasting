package com.lindzh.hasting.rpc.cluster1;

/**
 * Created by lin on 2016/12/9.
 */
public class ConsumeRpcObject {

    /**
     * 消费者所属应用
     */
    private String application;

    /**
     * 消费者ip
     */
    private String ip;

    /**
     * 依赖class
     */
    private String className;

    /**
     * 依赖class版本
     */
    private String version;

    /**
     * 隔离组
     */
    private String group;

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
