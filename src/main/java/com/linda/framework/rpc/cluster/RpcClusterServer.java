package com.linda.framework.rpc.cluster;

import com.linda.framework.rpc.Service;
import com.linda.framework.rpc.net.AbstractRpcAcceptor;
import com.linda.framework.rpc.net.RpcNetBase;
import com.linda.framework.rpc.net.RpcNetListener;
import com.linda.framework.rpc.server.SimpleRpcServer;

import java.util.Random;
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
	
	@Override
	public void register(Class<?> clazz, Object ifaceImpl) {
		super.register(clazz, ifaceImpl);
		this.doRegister(clazz, ifaceImpl);
	}

	@Override
	public void register(Class<?> clazz, Object ifaceImpl, String version) {
		super.register(clazz, ifaceImpl, version);
		this.doRegister(clazz, ifaceImpl, version);
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
				token = this.getToken();
			}
			this.addRpcFilter(new TokenFilter(this.getTimeout(),this.getToken(),this.isValidateToken()));
		}

		super.startService();
	}

	protected abstract void doRegister(Class<?> clazz, Object ifaceImpl);
	
	protected abstract void doRegister(Class<?> clazz, Object ifaceImpl, String version);

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
