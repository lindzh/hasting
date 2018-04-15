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
@Table(name = "service_consumer_info",autoGeneratePrimaryKey = true)
public class ServiceConsumerInfo {

    /**
     * id
     */
    @PrimaryKey
    private long id;

    /**
     * 服务
     */
    @Column(column = "service_id")
    @Index(name="ServiceId")
    private long serviceId;

    /**
     * 服务所属appid
     */
    @Column(column = "service_app_id")
    @Index(name="ServiceAppId")
    private long serviceAppId;

    /**
     * 依赖机器所属appid
     */
    @Column(column = "consumer_app_id")
    @Index(name="ConsumerAppId")
    private long consumerAppId;

    /**
     * 应用主机
     */
    @Column(column = "consumer_host_id")
    @Index(name="ConsumerHostId")
    private long comsumerHostId;

    /**
     * 消费者上线时间
     */
    @Column(column = "consumer_host_uptime")
    private long time;
}
