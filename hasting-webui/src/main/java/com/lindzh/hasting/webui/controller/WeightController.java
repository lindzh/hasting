package com.lindzh.hasting.webui.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.lindzh.hasting.cluster.JSONUtils;
import com.lindzh.hasting.webui.biz.AppService;
import com.lindzh.hasting.webui.biz.HostService;
import com.lindzh.hasting.webui.pojo.AppInfo;
import com.lindzh.hasting.webui.pojo.HostInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by lin on 2016/12/26.
 */
@Controller
public class WeightController extends BasicController{

    @Resource
    private AppService appService;

    @Resource
    private HostService hostService;

    /**
     * 权重页面
     * @param appId
     * @param limit
     * @param offset
     * @param model
     * @return
     */
    @RequestMapping(value="/weight/list",method = RequestMethod.GET)
    public String weightList(@RequestParam(value="appId",required = false,defaultValue = "0") long appId,
                             @RequestParam(value="limit",required = false,defaultValue = "50") int limit,
                             @RequestParam(value="offset",required = false,defaultValue = "0") int offset,ModelMap model){
        List<HostInfo> hosts = hostService.getListByAppIdWithPage(appId, limit, offset);
        hostService.setApps(hosts);
        model.put("hosts",hosts);
        model.put("limit",limit);
        model.put("offset",offset);
        model.put("appId",appId);
        model.put("total",hostService.getCountByAppId(appId));
        this.setApps(model);
        this.setApp(appId,model);
        return "weight_list";
    }

    /**
     * 权重编辑页面
     * @param appId
     * @param model
     * @return
     */
    @RequestMapping(value="weight/edit/{appId}",method = RequestMethod.GET)
    public String editWeightPage(@PathVariable("appId") long appId, ModelMap model){
        AppInfo app = appService.getById(appId);
        if(app!=null){
            model.put("app",app);

            List<HostInfo> hosts = hostService.getListByAppId(appId);
            model.put("hosts",hosts);
            model.put("hostCount",hosts.size());
        }
        return "weight_edit";
    }

    /**
     * 权重修改提交
     * @param appId
     * @param data
     * @param model
     * @return
     */
    @RequestMapping(value="weight/edit/{appId}",method = RequestMethod.POST)
    public String weightEditSubmmit(@PathVariable("appId") long appId, @RequestParam("data") String data, ModelMap model){
        List<HostInfo> hosts = JSONUtils.fromJSON(data, new TypeReference<List<HostInfo>>() {});
        for(HostInfo info:hosts){
            HostInfo hostInfo = hostService.getById(info.getId(), false);
            if(hostInfo==null){
                continue;
            }
            if(hostInfo.getAppId()!=appId){
                continue;
            }
            if(hostInfo.getWantWeight()==info.getWantWeight()){
                continue;
            }
            hostInfo.setWantWeight(info.getWantWeight());
            hostService.updateHost(hostInfo);
        }
        return "redirect:/weight/list?appId="+appId;
    }

}
