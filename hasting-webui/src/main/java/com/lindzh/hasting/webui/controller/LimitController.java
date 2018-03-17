package com.lindzh.hasting.webui.controller;

import com.lindzh.hasting.webui.biz.AppService;
import com.lindzh.hasting.webui.biz.LimitService;
import com.lindzh.hasting.webui.pojo.AppInfo;
import com.lindzh.hasting.webui.pojo.LimitInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Administrator on 2017/2/15.
 */
@Controller
@RequestMapping(value="/limit")
public class LimitController {

    @Resource
    private LimitService limitService;

    @Resource
    private AppService appService;

    /**
     * 限流列表
     * @param limit
     * @param offset
     * @param model
     * @return
     */
    @RequestMapping(value="/list",method = RequestMethod.GET)
    public String limitIndex(@RequestParam(value="limit",required = false,defaultValue = "50") int limit,
                       @RequestParam(value="offset",required = false,defaultValue = "0") int offset,ModelMap model){
        List<AppInfo> appList = appService.getAppList();
        model.put("appes",appList);
        model.put("limit",limit);
        model.put("offset",offset);
        model.put("total",appList.size());
        return "limit_list";
    }

    /**
     * 限流详情
     * @param appId
     * @param model
     * @return
     */
    @RequestMapping(value="/detail",method = RequestMethod.GET)
    public String limitDetail( @RequestParam(value="appId")long appId,ModelMap model){
        AppInfo app = appService.getById(appId);
        List<AppInfo> allApps = appService.getAppList();
        List<LimitInfo> limits = limitService.getListByAppId(appId, 1000, 0,true);
        model.put("app",app);
        model.put("limits",limits);
        model.put("total",limits.size());
        return "limit_detail";
    }

    /**
     * 限流编辑页面
     * @param appId
     * @param id
     * @param model
     * @return
     */
    @RequestMapping(value="/edit/{appId}/{id}",method = RequestMethod.GET)
    public String limitEdit( @PathVariable(value="appId")long appId,@PathVariable(value="id")long id,ModelMap model){
        if(id>0){
            LimitInfo limitInfo = limitService.getById(id);
            if(limitInfo.getAppId()!=appId){
                return "redirect:/limit/list";
            }
            model.put("info",limitInfo);
        }
        AppInfo app = appService.getById(appId);
        model.put("app",app);
        List<AppInfo> allApps = appService.getAppList();
        model.put("apps",allApps);
        model.put("appSize",allApps.size());
        return "limit_edit";
    }

    /**
     *
     * @param appId
     * @param id
     * @param limitType
     * @param limitAppId
     * @param service
     * @param method
     * @param ttl
     * @param count
     * @param model
     * @return
     */
    @RequestMapping(value="/edit/{appId}/{id}",method = RequestMethod.POST)
    public String editSubmit(@PathVariable(value="appId")long appId,
                             @PathVariable("id") long id,
                             @RequestParam("limitType") int limitType,
                             @RequestParam(value = "limitAppId",required = false,defaultValue = "0") long limitAppId,
                             @RequestParam(value = "service",required = false,defaultValue = "") String service,
                             @RequestParam(value = "method",required = false,defaultValue = "") String method,
                             @RequestParam("ttl") int ttl,
                             @RequestParam("count") int count,ModelMap model){
        LimitInfo info = limitService.getById(id);
        if(id==0){
            info = new LimitInfo();
            info.setAppId(appId);
        }
        if(info.getAppId()!=appId){
            return "redirect:/limit/list";
        }
        info.setTtl(limitType);
        info.setLimitAppId(limitAppId);
        if(StringUtils.isNotBlank(method)){
            info.setMethod(method.trim());
        }else{
            info.setMethod(null);
        }
        if(StringUtils.isNotBlank(service)){
            info.setService(service.trim());
        }else{
            info.setService(null);
        }
        info.setTtl(ttl);
        info.setCount(count);
        info.setUpdateTime(System.currentTimeMillis());
        limitService.addOrUpdateLimit(info);
        return "redirect:/limit/detail?appId="+appId;
    }

    /**
     * 清除
     * @param appId
     * @param id
     * @param model
     * @return
     */
    @RequestMapping(value="/del/{appId}/{id}",method = RequestMethod.GET)
    public String limitDel(@PathVariable(value="appId")long appId,
                           @PathVariable("id") long id,ModelMap model){
        LimitInfo limitInfo = limitService.getById(id);
        if(limitInfo.getAppId()!=appId){
            return "redirect:/limit/list";
        }
        limitService.deleteLimit(limitInfo,appId);
        return "redirect:/limit/detail?appId="+appId;
    }
}
