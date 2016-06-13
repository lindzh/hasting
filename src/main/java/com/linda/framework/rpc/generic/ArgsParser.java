package com.linda.framework.rpc.generic;

/**
 * 参数转换器
 * @author lindezhi
 * 2016年6月13日 下午4:40:52
 */
public interface ArgsParser {
	
	/**
	 * 入参转换
	 * @param argtype
	 * @param args
	 * @return
	 */
	public Object[] parseArgs(String[] argtype,Object[] args);
	
	/**
	 * 结果集转换
	 * @param result
	 * @return
	 */
	public Object parseResult(Object result);

	/**
	 * 参数类型检查
	 * @param argtype
	 * @param args
	 */
	public void checkArgs(String[] argtype,Object[] args);
}
