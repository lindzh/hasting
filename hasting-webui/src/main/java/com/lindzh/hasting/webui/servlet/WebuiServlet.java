package com.lindzh.hasting.webui.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.type.TypeReference;
import com.lindzh.hasting.cluster.JSONUtils;
import com.lindzh.hasting.webui.service.RpcConfig;
import com.lindzh.hasting.webui.service.RpcFetchService;
import com.lindzh.hasting.webui.service.RpcWebuiService;
import com.lindzh.hasting.webui.service.RpcWebuiServiceImpl;

/**
 * servlet config init parameter
 * rpcConfig:classpath rpcConfig
 * templateLocation:servlet context path freemarker template directory
 * templateSuffix:freemarker template suffix
 * @author lindezhi
 *
 */
public class WebuiServlet extends HttpServlet{
	
	private static final long serialVersionUID = 3170166525273679061L;

	private RpcWebuiService webuiService;
	
	private RpcFetchService fetchService;
	
	private ServletExceptionHandler exceptionHandler;
	
	private FreemarkerService freemarkerService;
	
	private RpcWebuiController webuiController;
	
	private Logger logger = Logger.getLogger(WebuiServlet.class);
	
	private String rpcConfig = "webui.json";
	
	/**
	 * 通过classpath获取servlet配置rpcConfig文件，初始化
	 * @param config
	 */
	private void initRpcConfig(ServletConfig config){
		String initRpcConfig = config.getInitParameter("rpcConfig");
		if(initRpcConfig!=null){
			rpcConfig = initRpcConfig;
		}
		InputStream ins = WebuiServlet.class.getClassLoader().getResourceAsStream(rpcConfig);
		try {
			String configJson = IOUtils.toString(ins, "utf-8");
			List<RpcConfig> configs = JSONUtils.fromJSON(configJson, new TypeReference<List<RpcConfig>>(){});
			logger.info("load rpc config from classpath:"+rpcConfig+" configs:"+configJson);
			fetchService = new RpcFetchService();
			webuiService = new RpcWebuiServiceImpl();
			fetchService.addInfoListener((RpcWebuiServiceImpl)webuiService);
			fetchService.setRpcConfigs(configs);
			fetchService.startService();
			logger.info("start rpc info service success");
		} catch (IOException e) {
			throw new RuntimeException("init rpcconfig of classpath:"+rpcConfig,e);
		}
	}
	
	private void initExceptionHandler(ServletConfig config){
		exceptionHandler = new SimpleServletExceptionHandler();
	}
	
	private void initController(ServletConfig config){
		webuiController = new RpcWebuiController();
		webuiController.setRpcWebuiService(webuiService);
	}
	
	/**
	 * 初始化freemarker，从servlet parameter templateLocation加载
	 * @param config
	 */
	private void initFreemarker(ServletConfig config){
		String templateLocation = config.getInitParameter("templateLocation");
		String templateSuffix = config.getInitParameter("templateSuffix");
		String location = config.getServletContext().getRealPath(templateLocation);
		freemarkerService = new FreemarkerService();
		freemarkerService.setLocation(location);
		freemarkerService.setSuffix(templateSuffix);
		freemarkerService.setExceptionHandler(exceptionHandler);
		logger.info("load freemarker template:"+templateLocation+" location:"+location+" suffix:"+templateSuffix);
		freemarkerService.startService();
		logger.info("start freemarker success");
	}
	
	/**
	 * servlet初始化配置
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		this.initRpcConfig(config);
		this.initExceptionHandler(config);
		this.initFreemarker(config);
		this.initController(config);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String uri = req.getRequestURI();
		HashMap<String, Object> model = new HashMap<String,Object>();
		String page = "404";
		String namespace = req.getParameter("namespace");
		try{
			if(uri.startsWith("/webui/services")){
				String keyword = req.getParameter("keyword");
				page = webuiController.search(namespace, keyword, model);
			}else if(uri.startsWith("/webui/hosts")){
				page = webuiController.getNamespaceHosts(namespace, model);
			}else if(uri.startsWith("/webui/service/hosts")){
				String serviceName = req.getParameter("serviceName");
				String serviceVersion = req.getParameter("serviceVersion");
				page = webuiController.getHostsByService(namespace, serviceName, serviceVersion, model);
			}else if(uri.startsWith("/webui/host/services")){
				String hostAndPort = req.getParameter("hostAndPort");
				page = webuiController.getHostServices(namespace, hostAndPort, model);
			}else if(uri.startsWith("/webui/configs")){
				page = webuiController.getRpcConfigs(model);
			}
			String result = freemarkerService.merge(page, model);
			ServletUtils.write(result, resp);
			return;
		}catch(Exception e){
			e.printStackTrace();
			page = "500";
		}
		int code = Integer.parseInt(page);
		ServletUtils.write(code, resp);
	}

	/**
	 * servlet生命周期结束
	 */
	@Override
	public void destroy() {
		super.destroy();
		if(fetchService!=null){
			fetchService.stopService();
		}
		if(freemarkerService!=null){
			freemarkerService.stopService();
		}
	}
	
}
