##RPC远程调用框架
提供一个端口多个服务同时高并发部署方案，同时提供过滤器支持。

###服务端

>添加远程服务

添加一个远程服务
<pre>
String host = "127.0.0.1";
int port = 4332;

RpcAcceptor acceptor = new RpcAcceptor();
acceptor.setHost(host);
acceptor.setPort(port);
RpcServiceProvider provider = new RpcServiceProvider();

SimpleServerRemoteExecutor proxy = new SimpleServerRemoteExecutor();

provider.setExecutor(proxy);

acceptor.addRpcCallListener(provider);

acceptor.startService();

logger.info("service started");
</pre>

>注册远程服务

简单注册多个远程服务
<pre>

Object obj = new HelloRpcServiceImpl();
proxy.registerRemote(HelloRpcService.class, obj);

HelloRpcTestServiceImpl obj2 = new HelloRpcTestServiceImpl();
proxy.registerRemote(HelloRpcTestService.class, obj2);
//注册登陆服务
LoginRpcService loginService = new LoginRpcServiceImpl();
proxy.registerRemote(LoginRpcService.class, loginService);
</pre>

>添加过滤器

添加ip端口 log和安全检查过滤器

<pre>
provider.getFilterChain().addRpcFilter(new MyTestRpcFilter());
provider.getFilterChain().addRpcFilter(new RpcLoginCheckFilter());
</pre>

###客户端

>注册远程服务

注册并启动
<pre>
String host = "127.0.0.1";
int port = 4332;
RpcConnector connector = new RpcConnector();
connector.setHost(host);
connector.setPort(port);

SimpleClientRemoteExecutor executor = new SimpleClientRemoteExecutor(connector);

SimpleClientRemoteProxy proxy = new SimpleClientRemoteProxy();

proxy.setRemoteExecutor(executor);

proxy.startService();

logger.info("start client");
</pre>

>注册为远程接口

添加远程调用bean

<pre>
LoginRpcService loginService = proxy.registerRemote(LoginRpcService.class);

HelloRpcService helloRpcService = proxy.registerRemote(HelloRpcService.class);

HelloRpcTestService testService = proxy.registerRemote(HelloRpcTestService.class);
</pre>

>调用远程服务

直接使用service

<pre>
helloRpcService.sayHello("this is HelloRpcService",564);

loginService.login("linda", "123456");

testService.index(43, "index client test");

//loginService.login("linda", "123456");

String hello = helloRpcService.getHello();

int ex = helloRpcService.callException(false);

int ex = helloRpcService.callException(true);//主动异常
</pre>