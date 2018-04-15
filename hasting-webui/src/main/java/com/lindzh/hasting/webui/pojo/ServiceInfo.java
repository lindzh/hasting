package com.lindzh.hasting.webui.pojo;

import com.lindzh.mybatis.generator.annotation.Column;
import com.lindzh.mybatis.generator.annotation.Index;
import com.lindzh.mybatis.generator.annotation.PrimaryKey;
import com.lindzh.mybatis.generator.annotation.Table;
import lombok.Data;

/**
 * Created by lin on 2016/12/15.
 */
@Data
@Table(name="service_info",autoGeneratePrimaryKey = true)
public class ServiceInfo {

    /**
     * 主键
     */
    @PrimaryKey
    private long id;

    /**
     * 服务名称
     */
    @Index(name="ServiceName")
    @Column(column="service_name")
    private String name;

    /**
     * 服务版本
     */
    @Column(column = "service_version")
    private String version;

    /**
     * 服务分组
     */
    @Column(column = "service_group")
    private String group;

    /**
     * 服务实现
     */
    @Column(column = "service_impl")
    private String impl;

    /**
     * 所属应用
     */
    @Column(column = "app_id")
    @Index(name="AppIdAndStatus")
    private long appId;

    /**
     * 状态
     */
    @Column(column = "service_status")
    @Index(name="AppIdAndStatus")
    private int status;

    /**
     * 服务提供者数量
     */
    @Column(column = "provider_count")
    private int providerCount;

    /**
     * 服务消费者数量
     */
    @Column(column = "consumer_count")
    private int consumerCount;

    //填充信息
    private AppInfo app;
}
