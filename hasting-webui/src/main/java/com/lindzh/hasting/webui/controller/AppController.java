package com.lindzh.hasting.webui.controller;

import com.lindzh.hasting.webui.biz.AppService;
import com.lindzh.hasting.webui.biz.HostService;
import com.lindzh.hasting.webui.biz.ServiceInfoService;
import com.lindzh.hasting.webui.pojo.AppInfo;
import com.lindzh.hasting.webui.pojo.HostInfo;
import com.lindzh.hasting.webui.pojo.ServiceInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by lin on 2016/12/23.
 */
@Controller
public class AppController extends BasicController{

    @Resource
    private AppService appService;

    @Resource
    private ServiceInfoService serviceInfoService;

    @Resource
    private HostService hostService;

    /**
     * app列表
     * @param model
     * @return
     */
    @RequestMapping(value="/app/list",method = RequestMethod.GET)
    public String appList(ModelMap model){
        List<AppInfo> appList = appService.getAppList();
        model.put("total",appList.size());
        model.put("appList",appList);
        return "app_list";
//        PackUtils.packModel(model);
//        return "json";
    }

    /**
     * app基本信息,提供服务情况,依赖服务情况
     * @param appId
     * @param model
     * @return
     */
    @RequestMapping(value="/app/info",method = RequestMethod.GET)
    public String appInfo(@RequestParam("appId") long appId, ModelMap model){

        AppInfo app = appService.getById(appId);
        if(app!=null){
            model.put("app",app);

            List<HostInfo> providers = hostService.getProviderListByAppId(appId);
            //提供者列表
            model.put("providers",providers);
            model.put("providerCount",providers.size());

            //提供服务列表
            List<ServiceInfo> services = serviceInfoService.getListByAppId(appId);
            model.put("provideServices",services);
            model.put("provideServiceCount",services.size());

            //依赖消费服务列表
            List<ServiceInfo> dependServices = serviceInfoService.getConsumeServicesByAppId(appId);
            serviceInfoService.setApp(dependServices);
            model.put("dependServices",dependServices);
            model.put("dependServiceCount",dependServices.size());
        }
        this.setApps(model);
        return "app_detail";
//        PackUtils.packModel(model);
//        return "json";
    }

    /**
     * 消费服务情况
     * @param appId
     * @param model
     * @return
     */
    @RequestMapping(value="/app/consumers",method = RequestMethod.GET)
    public String consumerInfo(@RequestParam("appId") long appId, ModelMap model){
        AppInfo app = appService.getById(appId);
        if(app!=null){
            model.put("app",app);

            //消费者机器列表
            List<HostInfo> consumerHosts = hostService.getConsumerListByAppId(appId);
            hostService.setApps(consumerHosts);
            model.put("consumerHosts",consumerHosts);
            model.put("consumerHostCount",consumerHosts.size());

            //消费app消费服务列表,按照app分类
            List<ServiceInfo> services = serviceInfoService.getConsumeServicesByAppId(appId);
            serviceInfoService.setApp(services);
            model.put("consumerServices",services);
            model.put("onsumerServiceCount",services.size());
        }
        this.setApps(model);
        return "app_consumers";
//        PackUtils.packModel(model);
//        return "json";
    }

    /**
     * 编辑app页面
     * @param appId
     * @param model
     * @return
     */
    @RequestMapping(value="/app/edit",method = RequestMethod.GET)
    public String editAppPage(@RequestParam("appId") long appId,ModelMap model){
        AppInfo app = appService.getById(appId);
        model.put("app",app);
        return "app_edit";
    }

    /**
     * 编辑app提交页面
     * @param appId
     * @param owner
     * @param email
     * @param desc
     * @param model
     * @return
     */
    @RequestMapping(value="/app/editsubmit",method = RequestMethod.POST)
    public String submitApp(@RequestParam("appId") long appId,String owner,String email,String desc,ModelMap model){
        AppInfo app = appService.getById(appId);
        if(app!=null){
            app.setEmail(email);
            app.setDesc(desc);
            app.setOwner(owner);
            appService.updateApp(app);
        }
        return "redirect:/app/list";
    }

}
