package com.lindzh.hasting.cluster.serialize;

import com.lindzh.hasting.rpc.RemoteCall;
import com.lindzh.hasting.cluster.HelloRpcService;
import com.lindzh.hasting.cluster.TestBean;

public class AbstractSerializer {

	public RemoteCall getCall(){
		String service = HelloRpcService.class.getName();
		String method = "getBean";
		String version = "534543";
		TestBean testBean = new TestBean();
		testBean.setLimit(4);
		testBean.setMessage("ggggggggggggggggggggggggggggggggggggggggggggg");
		testBean.setOffset(43432);
		testBean.setOrder("645gdfghdfghdf");
		RemoteCall call = new RemoteCall(service, method);
		call.setArgs(new Object[]{testBean,654645});
		call.setVersion(version);
		return call;
	}
}
