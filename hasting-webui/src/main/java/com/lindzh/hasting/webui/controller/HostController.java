package com.lindzh.hasting.webui.controller;

import com.lindzh.hasting.webui.biz.AppService;
import com.lindzh.hasting.webui.biz.HostService;
import com.lindzh.hasting.webui.biz.ServiceInfoService;
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
public class HostController extends BasicController {

    @Resource
    private HostService hostService;

    @Resource
    private ServiceInfoService serviceInfoService;

    @Resource
    private AppService appService;

    /**
     * 所有机器列表
     * @param appId
     * @param limit
     * @param offset
     * @param model
     * @return
     */
    @RequestMapping(value="/host/list",method = RequestMethod.GET)
    public String hostList(@RequestParam(value="appId",defaultValue = "0",required = false) long appId,
                           @RequestParam(value="limit",defaultValue = "50",required = false) int limit,
                           @RequestParam(value="offset",defaultValue = "0",required = false)int offset, ModelMap model){
        int total = hostService.getCountByAppId(appId);

        if(total>0){
            List<HostInfo> hosts = hostService.getListByAppIdWithPage(appId, limit, offset);
            hostService.setApps(hosts);
            model.put("hosts",hosts);
        }

        this.setApp(appId,model);
        model.put("appId",appId);
        model.put("total",total);
        model.put("limit",limit);
        model.put("offset",offset);
        this.setApps(model);
        return "host_list";
//        PackUtils.packModel(model);
//        return "json";
    }

    @RequestMapping(value="/host/detail",method = RequestMethod.GET)
    public String hostDetail(@RequestParam("hostId")long hostId,ModelMap model){
        HostInfo info = hostService.getById(hostId,true);
        if(info!=null){
            List<ServiceInfo> providerServices = serviceInfoService.getProvideServicesByHostId(hostId);

            List<ServiceInfo> consumeServices = serviceInfoService.getConsumeServicesByHostId(hostId);
            serviceInfoService.setApp(consumeServices);
            model.put("host",info);
            model.put("provideServices",providerServices);
            model.put("provideServiceCount",providerServices.size());

            model.put("consumeServices",consumeServices);
            model.put("consumeServiceCount",consumeServices.size());
        }
        this.setApps(model);
        return "host_detail";
//        PackUtils.packModel(model);
//        return "json";
    }

}
