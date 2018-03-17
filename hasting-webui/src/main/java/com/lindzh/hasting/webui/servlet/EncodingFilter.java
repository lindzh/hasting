package com.lindzh.hasting.webui.servlet;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.log4j.Logger;

public class EncodingFilter implements Filter{
	
	private String characterEncoding = "utf-8";
	
	private Logger logger = Logger.getLogger(EncodingFilter.class);

	@Override
	public void destroy() {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		request.setCharacterEncoding(characterEncoding);
		response.setCharacterEncoding(characterEncoding);
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		String encoding = config.getInitParameter("encoding");
		if(encoding!=null){
			Charset charset = Charset.forName(encoding);
			if(charset==null){
				throw new RuntimeException("not support encoding:"+encoding);
			}
			this.characterEncoding = encoding;
		}
		logger.info("set servlet encoding:"+this.characterEncoding);
	}

}
