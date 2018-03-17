package com.lindzh.hasting.webui.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lin on 2016/12/24.
 */
public class PackUtils {
    public static final String KEY           = "data";
    public static final String JSON_TEMPLATE = "{\"code\":%d,\"message\":\"%s\"}";

    public static void packResultMap(Map<String, Object> model, int code, String message, Object result) {
        model.clear();

        model.put("code", code);
        model.put("message", message);
        model.put(KEY, result);
    }

    public static void packFailure(Map<String, Object> model, String message) {
        packFailure(model, message, "");
    }

    public static void packFailure(Map<String, Object> model, String message, Object result) {
        model.clear();
        model.put("code", Const.CODE_PARAM_ERROR);
        model.put("message", message);
        model.put(KEY, result);
    }

    public static void packOk(Map<String, Object> model, Object result) {
        model.clear();
        model.put("message", "");
        model.put("code", Const.CODE_SUCCESS);
        model.put(KEY, result);
    }

    public static void packOk(Map<String, Object> model, List<?> list) {
        model.clear();

        model.put("code", Const.CODE_SUCCESS);

        Map<String, Object> ret = new HashMap<String, Object>();
        ret.put("total", list.size());
        ret.put("list", list);

        model.put(KEY, ret);
    }

    public static void packOk(Map<String, Object> model, List<?> list, int total) {
        model.clear();

        model.put("code", Const.CODE_SUCCESS);

        Map<String, Object> ret = new HashMap<String, Object>();
        ret.put("total", total);
        ret.put("list", list);

        model.put(KEY, ret);
    }

    public static void packOk(Map<String, Object> model) {
        model.clear();
        model.put(KEY, "ok");
        model.put("code", Const.CODE_SUCCESS);
    }

    public static void packOkMessage(Map<String, Object> model, String message) {
        model.clear();
        model.put(KEY, true);
        model.put("message", message);
        model.put("code", Const.CODE_SUCCESS);
    }

    public static void packFailureMessage(Map<String, Object> model, String message) {
        model.clear();
        model.put(KEY, false);
        model.put("code", Const.CODE_PARAM_ERROR);
        model.put("message", message);

    }

    public static void packMessageMap(Map<String, Object> model, int code, String message) {
        model.clear();
        model.put("code", code);
        model.put("message", message);
    }

    public static void packModel(Map<String, Object> model){
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.putAll(model);
        model.clear();
        packOk(model,result);
    }
}
