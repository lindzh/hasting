package com.lindzh.hasting.webui;

import com.lindzh.mybatis.generator.bean.MybatisPojo;
import com.lindzh.mybatis.generator.processor.DefaultMybatisGenerator;
import com.lindzh.hasting.webui.pojo.LimitInfo;

/**
 * Created by lin on 2016/12/15.
 */
public class AtoGenDao {

    public static void main(String[] args) {
        DefaultMybatisGenerator generator = new DefaultMybatisGenerator();
        generator.startService();
        MybatisPojo code = generator.genCode(LimitInfo.class, "com.linda.rpc.webui.dao", "D:\\Work\\java\\rpc-webui\\src\\main\\resources\\sqlmap\\", "D:\\Work\\java\\rpc-webui\\src\\main\\java\\com\\linda\\rpc\\webui\\dao\\");
        System.out.println("===========gen finished==================");
    }
}
