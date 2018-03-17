package com.lindzh.hasting.cluster.simple;

import com.lindzh.hasting.cluster.JSONUtils;
import com.lindzh.hasting.cluster.limit.LimitConst;
import com.lindzh.hasting.cluster.limit.LimitDefine;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/2/15.
 */
public class LimitBuilder {
    public static void main(String[] args) {
        ArrayList<LimitDefine> limitDefines = new ArrayList<LimitDefine>();
        LimitDefine define = new LimitDefine();
        define.setType(LimitConst.LIMIT_ALL);
        define.setCount(5);
        define.setTtl(7000);
        limitDefines.add(define);
        System.out.println(JSONUtils.toJSON(limitDefines));
    }
}
