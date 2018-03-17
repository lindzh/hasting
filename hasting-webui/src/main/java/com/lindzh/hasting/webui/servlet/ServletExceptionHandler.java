package com.lindzh.hasting.webui.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ServletExceptionHandler {
	
	public void handleException(HttpServletRequest request,HttpServletResponse response,Exception e);

}
