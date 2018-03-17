package com.lindzh.hasting.webui.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class ServletUtils {
	
	private static Logger logger = Logger.getLogger(ServletUtils.class);
	
	public static void write(String content,HttpServletResponse response){
		try {
			PrintWriter writer = response.getWriter();
			writer.print(content);
			writer.flush();
		} catch (IOException e) {
			logger.error("write content error ",e);
		}
	}
	
	public static void write(int code,HttpServletResponse response){
		response.setStatus(code);
	}
}
