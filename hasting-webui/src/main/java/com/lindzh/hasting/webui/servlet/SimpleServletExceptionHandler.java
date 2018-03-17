package com.lindzh.hasting.webui.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class SimpleServletExceptionHandler implements ServletExceptionHandler{
	
	private Logger logger = Logger.getLogger(SimpleServletExceptionHandler.class);

	@Override
	public void handleException(HttpServletRequest request, HttpServletResponse response, Exception e) {
		StringBuffer url = request.getRequestURL();
		logger.error(url.toString(),e);
	}

}
