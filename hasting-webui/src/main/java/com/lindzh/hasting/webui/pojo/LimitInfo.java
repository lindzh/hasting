package com.lindzh.hasting.webui.pojo;

import com.linda.common.mybatis.generator.annotation.Column;
import com.linda.common.mybatis.generator.annotation.Index;
import com.linda.common.mybatis.generator.annotation.PrimaryKey;
import com.linda.common.mybatis.generator.annotation.Table;
import lombok.Data;

/**
 * Created by Administrator on 2017/2/15.
 * 限流bean定义
 */
@Data
@Table(name="limit_info",autoGeneratePrimaryKey = true)
public class LimitInfo {

    @PrimaryKey(column = "id")
    private long id;

    /**
     * 所属应用
     */
    @Index(name = "AppId")
    @Column(column = "app_id")
    private long appId;

    /**
     * 限流类型
     */
    @Column(column = "limit_type")
    private int type;

    /**
     * 访问应用
     */
    @Column(column = "limit_application_id")
    private long limitAppId;

    /**
     * 限流服务
     */
    @Column(column = "limit_service")
    private String service;

    /**
     * 限流方法
     */
    @Column(column = "limit_service_method")
    private String method;

    /**
     * 限流时长 单位ms
     */
    @Column(column = "limit_ttl")
    private int ttl;

    /**
     * 访问量
     */
    @Column(column = "limit_count")
    private int count;

    /**
     * 添加时间
     */
    @Column(column = "update_time")
    private long updateTime;

    /**
     * 访问应用app
     */
    private AppInfo limitAppInfo;

}
