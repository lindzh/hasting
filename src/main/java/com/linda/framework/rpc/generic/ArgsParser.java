package com.linda.framework.rpc.generic;

public interface ArgsParser {
	
	public Object[] parseArgs(String[] argtype,Object[] args);
	
	public Object parseResult(Object result);

	public void checkArgs(String[] argtype,Object[] args);
}
