package com.lindzh.hasting.spring;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public abstract class AbstractTestCase {

	private ApplicationContext apc;
	
	public abstract List<String> getLocations();
	
	private void setIoc(){
		List<Field> fields = getResourceFields(this.getClass());
		for(Field field:fields){
			Resource resource = field.getAnnotation(Resource.class);
			if(resource!=null){
				try {
					field.setAccessible(true);
					field.set(this,apc.getBean(field.getType()));
				} catch (BeansException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private List<Field> getResourceFields(Class clazz){
		ArrayList<Field> list = new ArrayList<Field>();
		if(clazz!=AbstractTestCase.class&&clazz!=Object.class){
			Field[] fields = clazz.getDeclaredFields();
			for(Field field:fields){
				Resource resource = field.getAnnotation(Resource.class);
				if(resource!=null){
					list.add(field);
				}
			}
		}else{
			return Collections.EMPTY_LIST;
		}
		list.addAll(getResourceFields(clazz.getSuperclass()));
		return list;
	}
	
	@Before
	public void initSuper(){
		apc = new ClassPathXmlApplicationContext(getLocations().toArray(new String[0]));
		setIoc();
	}
	
	
	public <T> T getBean(String name,Class<T> type){
		return apc.getBean(name, type);
	}

}
