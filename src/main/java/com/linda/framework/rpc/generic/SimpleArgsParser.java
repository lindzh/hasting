package com.linda.framework.rpc.generic;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import com.linda.framework.rpc.exception.RpcException;

public class SimpleArgsParser implements ArgsParser{

	@Override
	public Object[] parseArgs(String[] argtype, Object[] args) {
		Object[] nargs = new Object[args.length];
		if(argtype!=null){
			int index = 0;
			for(String type:argtype){
				nargs[index] = this.parseArg(type, args[index]);
				index++;
			}
		}
		return nargs;
	}
	
	private void setProperties(Object object,Class clazz,Map<String,Object> params){
		if(params!=null){
			Set<String> keys = params.keySet();
			for(String key:keys){
				try {
					Field field = clazz.getField(key);
					field.setAccessible(true);
					field.set(object, params.get(key));
				} catch (NoSuchFieldException e) {
					throw new RpcException(e);
				} catch (SecurityException e) {
					throw new RpcException(e);
				} catch (IllegalArgumentException e) {
					throw new RpcException(e);
				} catch (IllegalAccessException e) {
					throw new RpcException(e);
				}
			}
		}
	}
	
	private Object parseObject(String type, Object jsonObject){
		if(jsonObject instanceof Map){
			try {
				Class<?> clazz = Class.forName(type);
				Object object = clazz.newInstance();
				Map<String,Object> params = (Map<String,Object>)jsonObject;
				this.setProperties(object, clazz, params);
				return object;
			} catch (ClassNotFoundException e) {
				throw new RpcException("class not found "+type);
			} catch (InstantiationException e) {
				throw new RpcException("create instance of "+type+" with no arg constructor error");
			} catch (IllegalAccessException e) {
				throw new RpcException(" create illegalAccess "+type);
			}
		}else{
			throw new RpcException("the value of "+type+" must be a map");
		}
	}
	
	private Object parseJdkObject(String type, Object obj) {
		if (type.startsWith("java.lang.String")) {
			return (String) obj;
		} else if (type.startsWith("java.lang.Integer")) {
			return (Integer) obj;
		} else if (type.startsWith("java.lang.Long")) {
			return (Long) obj;
		} else if (type.startsWith("java.lang.Short")) {
			return (Short) obj;
		} else if (type.startsWith("java.lang.Boolean")) {
			return (Boolean) obj;
		} else if (type.startsWith("java.lang.Float")) {
			return (Float) obj;
		} else if (type.startsWith("java.lang.Double")) {
			return (Double) obj;
		} else if (type.startsWith("java.lang.Byte")) {
			return (Byte) obj;
		} else if (type.startsWith("java.lang.Character")) {
			return (Character) obj;
		} else if (type.equals("int")) {
			return (int) (Integer) obj;
		} else if (type.equals("long")) {
			return (long) (Long) obj;
		} else if (type.equals("short")) {
			return (short) (Short) obj;
		} else if (type.equals("boolean")) {
			return (boolean) (Boolean) obj;
		} else if (type.equals("float")) {
			return (float) (Float) obj;
		} else if (type.equals("double")) {
			return (double) (Double) obj;
		} else if (type.equals("byte")) {
			return (byte) (Byte) obj;
		} else if (type.equals("char")) {
			return (char) (Character) obj;
		}
		return this.parseObject(type, obj);
	}
	
	private void checkCollectionType(String type,Object arg){
		if(type.startsWith("java.util.")){
			return;
		}else{
			throw new RpcException("the value of must be a map");
		}
	}
	
	private String getType(String type){
		int index = type.indexOf('<');
		if(index>0){
			int end = type.indexOf('>');
			if(end>index){
				String str = type.substring(index+1, end);
				index = str.indexOf(',');
				if(index>0){
					return str.substring(index+1, str.length());
				}else{
					return str;
				}
			}
		}
		index = type.indexOf("[");
		if(index>0){
			return type.substring(0, index);
		}
		return type;
	}
	
	private Object parseArg(String type, Object arg) {
		if (type.startsWith("java.util") || type.endsWith("[]")) {
			Class<? extends Object> argClass = arg.getClass();
			if (arg instanceof List) {
				this.checkCollectionType(type, arg);
				List<Object> argList = (List<Object>) arg;
				List<Object> nargList = new ArrayList<Object>();
				String paramType = this.getType(type);
				for (Object a : argList) {
					Object object = this.parseJdkObject(paramType, a);
					nargList.add(object);
				}
				return nargList;
			} else if (arg instanceof Set) {
				this.checkCollectionType(type, arg);
				Set<Object> list = (Set<Object>) arg;
				Set<Object> nargList = new HashSet<Object>();
				String paramType = this.getType(type);
				for (Object a : list) {
					Object object = this.parseJdkObject(paramType, a);
					nargList.add(object);
				}
				return nargList;
			} else if (arg instanceof Map) {
				this.checkCollectionType(type, arg);
				Map<String, Object> list = (Map<String, Object>) arg;
				Map<String, Object> nargList = new HashMap<String, Object>();
				String paramType = this.getType(type);
				Set<String> keys = list.keySet();
				for (String key : keys) {
					Object a = list.get(key);
					if (a != null) {
						Object object = this.parseJdkObject(paramType, a);
						nargList.put(key, object);
					} else {
						nargList.put(key, a);
					}
				}
				return nargList;
			} else if (arg instanceof Iterable) {
				this.checkCollectionType(type, arg);
				Iterable<Object> list = (Iterable<Object>) arg;
				List<Object> nargList = new ArrayList<Object>();
				String paramType = this.getType(type);
				for (Object a : list) {
					Object object = this.parseJdkObject(paramType, a);
					nargList.add(object);
				}
				return nargList;
			} else if (arg instanceof Stack) {
				this.checkCollectionType(type, arg);
				Stack<Object> list = (Stack<Object>) arg;
				Stack<Object> nargList = new Stack<Object>();
				String paramType = this.getType(type);
				for (Object a : list) {
					Object object = this.parseJdkObject(paramType, a);
					nargList.add(object);
				}
				return nargList;
			} else if (arg instanceof Queue) {
				this.checkCollectionType(type, arg);
				Queue<Object> queue = (Queue<Object>) arg;
				LinkedList<Object> nargList = new LinkedList<Object>();
				String paramType = this.getType(type);
				for (Object a : queue) {
					Object object = this.parseJdkObject(paramType, a);
					nargList.add(object);
				}
				return nargList;
			} else if (type.endsWith("[]")) {
				Object[] array = (Object[]) arg;
				List<Object> nargList = new ArrayList<Object>();
				String paramType = this.getType(type);
				for (Object a : array) {
					Object object = this.parseJdkObject(paramType, a);
					nargList.add(object);
				}
				return nargList.toArray();
			}
			throw new RpcException("not supported type:"+type+" :"+argClass);
		} else {
			return this.parseJdkObject(type, arg);
		}
	}
	
	private Object parseObject(Object obj){
		if(obj.getClass()==int.class){
			return obj;
		}else if(obj.getClass()==Integer.class){
			return obj;	
		}else if(obj.getClass()==long.class){
			return obj;
		}else if(obj.getClass()==Long.class){
			return obj;
		}else if(obj.getClass()==short.class){
			return obj;
		}else if(obj.getClass()==Short.class){
			return obj;
		}else if(obj.getClass()==double.class){
			return obj;
		}else if(obj.getClass()==Double.class){
			return obj;
		}else if(obj.getClass()==float.class){
			return obj;
		}else if(obj.getClass()==Float.class){
			return obj;
		}else if(obj.getClass()==boolean.class){
			return obj;
		}else if(obj.getClass()==Boolean.class){
			return obj;
		}else if(obj.getClass()==byte.class){
			return obj;
		}else if(obj.getClass()==Byte.class){
			return obj;
		}else if(obj.getClass()==char.class){
			return obj;
		}else if(obj.getClass()==Character.class){
			return obj;
		}else if(obj.getClass()==String.class){
			return obj;
		}else{
			return obj;
		}
	}

	@Override
	public Object parseResult(Object result) {
		
		return result;
	}

	@Override
	public void checkArgs(String[] argtype, Object[] args) {
		if((argtype==null&&args==null)){
			return;
		}else if(argtype!=null&&args!=null){
			if(argtype.length==args.length){
				return ;
			}
		}
		throw new RpcException("method args error");
	}
}
