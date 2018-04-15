package com.lindzh.hasting.webui.pojo;

import com.lindzh.mybatis.generator.annotation.Column;
import com.lindzh.mybatis.generator.annotation.PrimaryKey;
import com.lindzh.mybatis.generator.annotation.Table;
import lombok.Data;

/**
 * Created by lin on 2016/12/15.
 */
@Data
@Table(name="service_provider_info",autoGeneratePrimaryKey = true)
public class ServiceProviderInfo {

    /**
     * id
     */
    @PrimaryKey
    private long id;

    /**
     * 服务
     */
    @Column(column = "service_id")
    private long serviceId;

    /**
     * 应用,服务提供者的app和service app一致
     */
    @Column(column = "app_id")
    private long appId;

    /**
     * 应用主机
     */
    @Column(column = "host_id")
    private long hostId;


    /**
     * 时间
     */
    @Column(column = "provider_host_uptime")
    private long time;
}
