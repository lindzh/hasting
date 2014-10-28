##RPC远程调用框架
rpc远程调用通用框架，提供一个端口多个服务同时高并发部署方案，同时提供安全，接口访问频率基础过滤器支持。

###服务端

>添加远程服务

添加一个远程服务
```java
String host = "127.0.0.1";
int port = 4332;
//添加一个网络接口监听器
AbstractRpcAcceptor acceptor = new RpcOioAcceptor();
//nio AbstractRpcAcceptor acceptor = new RpcNioAcceptor();
//设置监听端口
acceptor.setHost(host);
acceptor.setPort(port);
//添加服务提供者
RpcServiceProvider provider = new RpcServiceProvider();
SimpleServerRemoteExecutor proxy = new SimpleServerRemoteExecutor();
//设置反射服务执行器
provider.setExecutor(proxy);
//添加网络通知监听器
acceptor.addRpcCallListener(provider);
//启动服务
acceptor.startService();
logger.info("service started");
```

>注册远程服务

简单注册多个远程服务
```java
Object obj = new HelloRpcServiceImpl();
proxy.registerRemote(HelloRpcService.class, obj);

HelloRpcTestServiceImpl obj2 = new HelloRpcTestServiceImpl();
proxy.registerRemote(HelloRpcTestService.class, obj2);
//注册登陆服务
LoginRpcService loginService = new LoginRpcServiceImpl();
proxy.registerRemote(LoginRpcService.class, loginService);
```

>添加过滤器

添加ip端口 log和安全检查过滤器

```java
provider.addRpcFilter(new MyTestRpcFilter());
provider.addRpcFilter(new RpcLoginCheckFilter());
```

###客户端

>注册远程服务

注册并启动
```java
String host = "127.0.0.1";
int port = 4332;
//添加连接器
AbstractRpcConnector connector = new RpcOioConnector();
//nio AbstractRpcConnector connector = new RpcNioConnector();
//设置远程服务
connector.setHost(host);
connector.setPort(port);
//添加执行器
SimpleClientRemoteExecutor executor = new SimpleClientRemoteExecutor(connector);
//添加反射代理
SimpleClientRemoteProxy proxy = new SimpleClientRemoteProxy();
proxy.setRemoteExecutor(executor);
//启动服务
proxy.startService();

logger.info("start client");
```

>注册为远程接口

添加远程调用bean

```java
LoginRpcService loginService = proxy.registerRemote(LoginRpcService.class);

HelloRpcService helloRpcService = proxy.registerRemote(HelloRpcService.class);

HelloRpcTestService testService = proxy.registerRemote(HelloRpcTestService.class);
```

>调用远程服务

直接使用service

```java
helloRpcService.sayHello("this is HelloRpcService",564);

loginService.login("linda", "123456");

testService.index(43, "index client test");

//loginService.login("linda", "123456");

String hello = helloRpcService.getHello();

int ex = helloRpcService.callException(false);

int ex = helloRpcService.callException(true);//主动异常
```