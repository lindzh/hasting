package com.lindzh.hasting.webui.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by lin on 2016/12/24.
 */
public class HealthServlet extends HttpServlet{

    private static final long serialVersionUID = -6257404250129290575L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException {

        String content = "{\"code\":200,\"message\":\"success\",\"data\":"+System.currentTimeMillis()+"}";
        this.print(req, resp, content);
    }

    private void print(HttpServletRequest req,HttpServletResponse response, String content) {
        //添加js调用支持
        try {
            response.getWriter().print(content);
            response.getWriter().flush();
        } catch (IOException e) {

        }
    }
}