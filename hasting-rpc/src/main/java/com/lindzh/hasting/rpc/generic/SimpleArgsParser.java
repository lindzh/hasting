package com.lindzh.hasting.rpc.generic;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import com.lindzh.hasting.rpc.exception.RpcException;
import com.lindzh.hasting.rpc.utils.RpcUtils;

/**
 * 参数转换器实现
 * @author lindezhi
 * 2016年6月13日 下午4:42:35
 */
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
					Field field = clazz.getDeclaredField(key);
					int mod = field.getModifiers();
					//final不可修改字段去掉
					if(Modifier.isFinal(mod)){
						continue;
					}
					field.setAccessible(true);
					Object vvv = params.get(key);
					Class<?> type = field.getType();
					Object arg = this.parseArg(type.getCanonicalName(), vvv);
					field.set(object, arg);
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
	
	private String[] getType(String type){
		int start = type.indexOf('<');
		int end = type.lastIndexOf('>');
		if(start>0&&end>start){
			int bb = type.indexOf(',', start);
			if(bb>0){
				String ss = type.substring(start, bb);
				String s2 = type.substring(bb+2, end);
				return new String[]{ss,s2};
			}else{
				return new String[]{type.substring(start+1, end)};
			}
		}
		throw new RpcException("unknown type:"+type);
	}
	
	private Object parseArg(String type, Object arg) {
		if (type.startsWith("java.util") || type.endsWith("[]")) {
			Class<? extends Object> argClass = arg.getClass();
			if (arg instanceof List) {
				this.checkCollectionType(type, arg);
				List<Object> argList = (List<Object>) arg;
				List<Object> nargList = new ArrayList<Object>();
				String paramType = this.getType(type)[0];
				for (Object a : argList) {
					Object object = this.parseJdkObject(paramType, a);
					nargList.add(object);
				}
				return nargList;
			} else if (arg instanceof Set) {
				this.checkCollectionType(type, arg);
				Set<Object> list = (Set<Object>) arg;
				Set<Object> nargList = new HashSet<Object>();
				String paramType = this.getType(type)[0];
				for (Object a : list) {
					Object object = this.parseJdkObject(paramType, a);
					nargList.add(object);
				}
				return nargList;
			} else if (arg instanceof Map) {
				this.checkCollectionType(type, arg);
				Map<Object, Object> list = (Map<Object, Object>) arg;
				Map<Object, Object> nargList = new HashMap<Object, Object>();
				String[] mapType = this.getType(type);
				Set<Object> keys = list.keySet();
				for (Object key : keys) {
					Object k = this.parseArg(mapType[0], nargList);
					Object a = list.get(key);
					if (a != null) {
						Object v = this.parseJdkObject(mapType[1], a);
						nargList.put(k, v);
					} else {
						nargList.put(k, null);
					}
				}
				return nargList;
			} else if (arg instanceof Iterable) {
				this.checkCollectionType(type, arg);
				Iterable<Object> list = (Iterable<Object>) arg;
				List<Object> nargList = new ArrayList<Object>();
				String paramType = this.getType(type)[0];
				for (Object a : list) {
					Object object = this.parseJdkObject(paramType, a);
					nargList.add(object);
				}
				return nargList;
			} else if (arg instanceof Stack) {
				this.checkCollectionType(type, arg);
				Stack<Object> list = (Stack<Object>) arg;
				Stack<Object> nargList = new Stack<Object>();
				String paramType = this.getType(type)[0];
				for (Object a : list) {
					Object object = this.parseJdkObject(paramType, a);
					nargList.add(object);
				}
				return nargList;
			} else if (arg instanceof Queue) {
				this.checkCollectionType(type, arg);
				Queue<Object> queue = (Queue<Object>) arg;
				LinkedList<Object> nargList = new LinkedList<Object>();
				String paramType = this.getType(type)[0];
				for (Object a : queue) {
					Object object = this.parseJdkObject(paramType, a);
					nargList.add(object);
				}
				return nargList;
			} else if (arg.getClass().isArray()) {
				Class<? extends Object> class1 = arg.getClass();
				Class<?> componentType = class1.getComponentType();
				if(componentType==int.class){
					return arg;
				}else if(componentType==long.class){
					return arg;
				}else if(componentType==short.class){
					return arg;
				}else if(componentType==double.class){
					return arg;
				}else if(componentType==float.class){
					return arg;
				}else if(componentType==char.class){
					return arg;
				}else if(componentType==boolean.class){
					return arg;
				}else if(componentType==byte.class){
					return arg;
				}else{
					//数组类型 bug
					Object[] array = (Object[]) arg;
					Object result = Array.newInstance(componentType,array.length);
					int index = 0;
					String paramType = componentType.getCanonicalName();
					for (Object a : array) {
						Object object = this.parseJdkObject(paramType, a);
						Array.set(result,index,object);
						index++;
					}
					return result;
				}
			}
			throw new RpcException("not supported type:"+type+" :"+argClass);
		} else {
			return this.parseJdkObject(type, arg);
		}
	}
	
	private Map<String,Object> parseObjectToMap(Object obj){
		HashMap<String,Object> map = new HashMap<String,Object>();
		Class<? extends Object> clazz = obj.getClass();
		List<Field> fields = RpcUtils.getFields(clazz);
		for(Field f:fields){
			f.setAccessible(true);
			int mod = f.getModifiers();
			//final 不可修改字段去掉
			if(Modifier.isFinal(mod)){
				continue;
			}
			String name = f.getName();
			try {
				Object v = f.get(obj);
				Object object = this.parseResult(v);
				map.put(name, object);
			} catch (IllegalArgumentException e) {
				throw new RpcException(e);
			} catch (IllegalAccessException e) {
				throw new RpcException(e);
			}
		}
		return map;
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
			return this.parseObjectToMap(obj);
		}
	}

	@Override
	public Object parseResult(Object result) {
		if(result==null){
			return null;
		}
		if(result instanceof List){
			List<Object> list = new ArrayList<Object>();
			List<Object> is = (List<Object>)result;
			for(Object i:is){
				Object object = this.parseResult(i);
				list.add(object);
			}
			return list;
		}else if(result instanceof Set){
			Set<Object> list = new HashSet<Object>();
			Set<Object> is = (Set<Object>)result;
			for(Object i:is){
				Object object = this.parseResult(i);
				list.add(object);
			}
			return list;
		}else if(result instanceof Map){
			Map<Object,Object> map = new HashMap<Object,Object>();
			Map<Object,Object> is = (Map<Object,Object>)result;
			Set<Object> keys = is.keySet();
			for(Object i:keys){
				Object key = this.parseResult(i);
				Object vv = is.get(i);
				if(vv!=null){
					Object object = this.parseResult(vv);
					map.put(key, object);
				}else{
					map.put(key, null);
				}
			}
			return map;
		}else if(result instanceof Stack){
			List<Object> list = new LinkedList<Object>();
			Stack<Object> is = (Stack<Object>)result;
			for(Object i:is){
				Object object = this.parseResult(i);
				list.add(object);
			}
			return list;
		}else if(result instanceof Queue){
			List<Object> list = new LinkedList<Object>();
			Queue<Object> is = (Queue<Object>)result;
			for(Object i:is){
				Object object = this.parseResult(i);
				list.add(object);
			}
			return list;
		}else if(result instanceof Iterable){
			List<Object> list = new ArrayList<Object>();
			Iterable<Object> is = (Iterable<Object>)result;
			for(Object i:is){
				Object object = this.parseResult(i);
				list.add(object);
			}
			return list;
		}else if(result.getClass().isArray()){//数组
			Class<? extends Object> class1 = result.getClass();
			Class<?> componentType = class1.getComponentType();
			if(componentType==int.class){
				return result;
			}else if(componentType==long.class){
				return result;
			}else if(componentType==short.class){
				return result;
			}else if(componentType==double.class){
				return result;
			}else if(componentType==float.class){
				return result;
			}else if(componentType==char.class){
				return result;
			}else if(componentType==boolean.class){
				return result;
			}else if(componentType==byte.class){
				return result;
			}else{
				List<Object> list = new ArrayList<Object>();
				Object[] is = (Object[])result;
				for(Object i:is){
					Object object = this.parseResult(i);
					list.add(object);
				}
				return list.toArray();
			}
		}
		return this.parseObject(result);
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
