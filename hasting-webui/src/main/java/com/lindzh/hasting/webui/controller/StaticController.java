package com.lindzh.hasting.webui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by lin on 2016/12/24.
 */
@Controller
public class StaticController {

    @RequestMapping(value="/{class1}/{file}.htm",method= RequestMethod.GET)
    public String getClassifiedResource(@PathVariable("class1")String class1,
                                        @PathVariable("file")String file,ModelMap model){
        return "static/"+class1+"/"+file;
    }

    @RequestMapping(value="/{file}.htm",method=RequestMethod.GET)
    public String getStaticResource(@PathVariable("file")String file,ModelMap model){
        return "static/"+file;
    }
}
