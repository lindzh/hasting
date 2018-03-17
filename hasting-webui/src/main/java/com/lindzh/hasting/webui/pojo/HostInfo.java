package com.lindzh.hasting.webui.pojo;

import com.linda.common.mybatis.generator.annotation.Column;
import com.linda.common.mybatis.generator.annotation.Index;
import com.linda.common.mybatis.generator.annotation.PrimaryKey;
import com.linda.common.mybatis.generator.annotation.Table;
import lombok.Data;

/**
 * Created by lin on 2016/12/15.
 */
@Data
@Table(name="host_info",autoGeneratePrimaryKey = true)
public class HostInfo {

    /**
     * id
     */
    @PrimaryKey
    private long id;

    /**
     * ip
     */
    @Index(name="Host")
    @Column(column = "host_ip")
    private String host;

    /**
     * 端口
     */
    @Column(column = "host_port")
    private int port;

    /**
     * 所属应用
     */
    @Column(column = "app_id")
    @Index(name="AppIdAndStatus")
    private long appId;

    /**
     * 权重
     */
    @Column(column = "weight")
    private long weight;

    /**
     * 期望权重
     */
    @Column(column = "want_weight")
    private long wantWeight;

    /**
     * token
     */
    @Column(column = "token")
    private String token;

    /**
     * 状态
     */
    @Column(column = "host_status")
    @Index(name="AppIdAndStatus")
    private int status;

    /**
     * 上线时间
     */
    @Column(column = "up_time")
    private long time;


    private AppInfo app;

}
