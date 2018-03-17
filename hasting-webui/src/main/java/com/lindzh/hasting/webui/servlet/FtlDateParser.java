package com.lindzh.hasting.webui.servlet;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import freemarker.template.SimpleNumber;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

public class FtlDateParser implements TemplateMethodModelEx{
	@Override
	public Object exec(List args) throws TemplateModelException {
		SimpleNumber num = (SimpleNumber)args.get(0);
		long time = num.getAsNumber().longValue();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return format.format(new Date(time));
	}
}
