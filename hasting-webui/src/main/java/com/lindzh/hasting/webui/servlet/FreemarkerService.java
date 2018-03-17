package com.lindzh.hasting.webui.servlet;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.log4j.Logger;

import com.lindzh.hasting.rpc.Service;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class FreemarkerService implements Service {

	private String location;

	private String suffix;

	private ServletExceptionHandler exceptionHandler;

	private Configuration cfg = new Configuration();
	
	private String encoding = "utf-8";
	
	private Logger logger = Logger.getLogger(FreemarkerService.class);
	
	public String merge(String template, Map<String, Object> model) {
		try {
			Template tpl = cfg.getTemplate(template+"."+suffix);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			OutputStreamWriter writer = new OutputStreamWriter(bos);
			tpl.process(model, writer);
			writer.flush();
			writer.close();
			byte[] byteArray = bos.toByteArray();
			return new String(byteArray,encoding);
		} catch (IOException e) {
			logger.error("IOException template "+template,e);
			throw new RuntimeException(e);
		} catch (TemplateException e) {
			logger.error("TemplateException template "+template,e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void startService() {
		cfg.setDefaultEncoding(encoding);
		try {
			cfg.setDirectoryForTemplateLoading(new File(location));
			cfg.setObjectWrapper(new DefaultObjectWrapper());
			cfg.setNumberFormat("#");
			logger.info("freemarker service started");
		} catch (IOException e) {
			logger.error("setDirectoryForTemplateLoading IOException location: "+location,e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void stopService() {
		cfg.clearTemplateCache();
		logger.info("freemarker service stoped");
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public ServletExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

	public void setExceptionHandler(ServletExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

}
