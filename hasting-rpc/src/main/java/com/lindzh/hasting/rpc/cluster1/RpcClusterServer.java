package com.lindzh.hasting.rpc.cluster1;

import com.lindzh.hasting.rpc.Service;
import com.lindzh.hasting.rpc.net.AbstractRpcAcceptor;
import com.lindzh.hasting.rpc.net.RpcNetListener;
import com.lindzh.hasting.rpc.server.SimpleRpcServer;
import com.lindzh.hasting.rpc.utils.RpcUtils;

import java.util.UUID;

/**
 * 集群配置管理服务支持，统一管理rpc服务
 * @author lindezhi
 * 及时通知管理服务器提供的rpc和状态
 */
public abstract class RpcClusterServer extends SimpleRpcServer implements RpcNetListener,Service{

	/**
	 * 是否校验token
	 */
	protected boolean validateToken = true;

	protected String token;

	protected boolean stop = false;
	
	@Override
	public void register(Class<?> clazz, Object ifaceImpl) {
		this.register(clazz, ifaceImpl, RpcUtils.DEFAULT_VERSION);
	}

	@Override
	public void register(Class<?> clazz, Object ifaceImpl, String version) {
		this.register(clazz, ifaceImpl, version,RpcUtils.DEFAULT_GROUP);
	}

	public void register(Class<?> clazz, Object ifaceImpl, String version,String group) {
		if(version == null){
			version=RpcUtils.DEFAULT_VERSION;
		}

		if(group == null){
			group=RpcUtils.DEFAULT_GROUP;
		}
		super.register(clazz, ifaceImpl, version,group);
		this.doRegister(clazz, ifaceImpl, version,group);
	}

	/**
	 * 生成随机token
	 * @return
     */
	protected String genToken(){
		UUID uuid = UUID.randomUUID();
		return uuid.toString().replace("-","").substring(0,8);
	}

	@Override
	public void startService() {
		//添加token验证器
		if(this.validateToken){
			if(token==null){
				token = this.genToken();
			}
			this.addRpcFilter(new TokenFilter(this.getTimeout(),this.getToken(),this.isValidateToken()));
		}

		super.startService();
		stop = false;
	}

	@Override
	public void stopService() {
		super.stopService();
		this.stop = true;
	}

	protected abstract void doRegister(Class<?> clazz, Object ifaceImpl, String version, String group);

	@Override
	public void setAcceptor(AbstractRpcAcceptor acceptor) {
		super.setAcceptor(acceptor);
		acceptor.addRpcNetListener(this);
	}

	public boolean isValidateToken() {
		return validateToken;
	}

	public void setValidateToken(boolean validateToken) {
		this.validateToken = validateToken;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
