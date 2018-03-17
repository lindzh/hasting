package com.lindzh.hasting.webui.controller;

import com.lindzh.hasting.webui.biz.AppService;
import com.lindzh.hasting.webui.pojo.AppInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by lin on 2016/12/23.
 */
@Controller
public class BasicController {

    @Resource
    private AppService appService;


    public void setApps(ModelMap model){
        List<AppInfo> appList = appService.getAppList();
        model.put("apps",appList);
    }

    public void setApp(long appId, ModelMap model){
        AppInfo app = appService.getById(appId);
        if(app==null){
            app = new AppInfo();
            app.setId(0);
            app.setName("全部");
        }
        model.put("app",app);
    }


}
