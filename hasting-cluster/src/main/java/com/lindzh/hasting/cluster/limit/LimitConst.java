package com.lindzh.hasting.cluster.limit;

/**
 * Created by lin on 2017/1/24.
 * 说明:限流全都在方法上
 */
public class LimitConst {

    /**
     * 全局限流
     */
    public static final int LIMIT_ALL = 0;

    /**
     * service限流
     */
    public static final int LIMIT_SERVICE = 1;

    /**
     * 方法限流
     */
    public static final int LIMIT_METHOD = 2;


    /**
     * 指定应用限流
     */
    public static final int LIMIT_APP_ALL = 10;

    /**
     * 指定应用限流
     */
    public static final int LIMIT_APP_SERVICE = 11;

    /**
     * 指定应用限流
     */
    public static final int LIMIT_APP_METHOD = 12;

    public static final String SYSTEM_LIMIT = "SYSTEM_DEFAULT";

    public static final String OUTER_LIMIT = "OUTER_DEFAULT";
}
