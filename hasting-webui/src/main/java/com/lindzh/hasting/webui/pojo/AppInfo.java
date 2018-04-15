package com.lindzh.hasting.webui.pojo;

import com.lindzh.mybatis.generator.annotation.Column;
import com.lindzh.mybatis.generator.annotation.Index;
import com.lindzh.mybatis.generator.annotation.PrimaryKey;
import com.lindzh.mybatis.generator.annotation.Table;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lin on 2016/12/15.
 */
@Data
@Table(name="app_info",autoGeneratePrimaryKey = true)
public class AppInfo {

    /**
     * id
     */
    @PrimaryKey(column = "id")
    private long id;

    /**
     * 应用名称
     */
    @Index(name = "AppName")
    @Column(column = "app_name")
    private String name;

    /**
     * 应用描述
     */
    @Column(column = "app_desc")
    private String desc;

    /**
     * 应用owner
     */
    @Column(column="app_owner")
    private String owner;

    /**
     * 应用owner email
     */
    @Column(column = "app_owner_email")
    private String email;

    /**
     * 限流同步状态
     */
    @Column(column = "limit_sync_status")
    private int limitSyncStatus;

    /**
     * 限流数量
     */
    @Column(column = "limit_count")
    private int limitCount;

    /**
     * 限流同步时间
     */
    @Column(column = "limit_sync_time")
    private long limitSyncTime;

    //作为dto使用
    private List<HostInfo> hosts = new ArrayList<HostInfo>();
    //作为DTO使用
    private List<ServiceInfo> services = new ArrayList<ServiceInfo>();


}
