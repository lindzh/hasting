package com.linda.framework.rpc;

import com.linda.framework.rpc.utils.RpcUtils.RpcType;

public interface RemoteExecutor extends Service{
	
	public void oneway(RemoteCall call);
	
	public Object invoke(RemoteCall call);
	
	public static final int ONEWAY = RpcType.ONEWAY.getType();
	
	public static final int INVOKE = RpcType.INVOKE.getType();

}
