##RPC远程调用框架
提供一个端口多个服务同时高并发部署方案，同时提供过滤器支持。

>简单的服务端代码
<pre>
String host = "127.0.0.1";
int port = 4332;

RpcAcceptor acceptor = new RpcAcceptor();
acceptor.setHost(host);
acceptor.setPort(port);
RpcServiceProvider provider = new RpcServiceProvider();

SimpleServerRemoteExecutor proxy = new SimpleServerRemoteExecutor();

Object obj = new HelloRpcServiceImpl();

proxy.registerRemote(HelloRpcService.class, obj);

HelloRpcTestServiceImpl obj2 = new HelloRpcTestServiceImpl();

proxy.registerRemote(HelloRpcTestService.class, obj2);

LoginRpcService loginService = new LoginRpcServiceImpl();

proxy.registerRemote(LoginRpcService.class, loginService);

provider.setExecutor(proxy);

provider.getFilterChain().addRpcFilter(new MyTestRpcFilter());

provider.getFilterChain().addRpcFilter(new RpcLoginCheckFilter());

acceptor.addRpcCallListener(provider);

acceptor.startService();

logger.info("service started");
</pre>

>客户端代码

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

LoginRpcService loginService = proxy.registerRemote(LoginRpcService.class);

HelloRpcService helloRpcService = proxy.registerRemote(HelloRpcService.class);

HelloRpcTestService testService = proxy.registerRemote(HelloRpcTestService.class);

logger.info("start client");

helloRpcService.sayHello("this is HelloRpcService",564);

loginService.login("linda", "123456");

testService.index(43, "index client test");

//loginService.login("linda", "123456");

String hello = helloRpcService.getHello();

int ex = helloRpcService.callException(false);
</pre>