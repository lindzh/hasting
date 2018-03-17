package com.lindzh.hasting.webui.utils;

/**
 * Created by lin on 2016/12/16.
 */
public class Const {

    public static final int HOST_STATUS_ON = 1;

    public static final int HOST_STATUS_OFF = 0;

    public static final int HOST_STATUS_ALL = -1;



    public static final int SERVICE_OK = 1;

    public static final int SERVICE_ERR = 0;

    public static final int SERVICE_ALL = -1;


    public static final int CODE_SUCCESS = 200;

    public static final int CODE_PARAM_ERROR = 309;

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

    /**
     * 应用限流状态 已经同步
     */
    public static final int APP_LIMIT_SYNCED = 1;

    /**
     * 应用限流状态 未同步
     */
    public static final int APP_LIMIT_SYNCED_NO = 0;
}
