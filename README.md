### 轻量级分布式服务化框架
#### 基本原理
![Alt text](http://img2.ph.126.net/oMJOdxjM93wKtlsF764_DQ==/6630102394491146555.jpg)

```
轻量级分部署服务调度框架的基本原理是服务提供方Provider提供rpc服务，同时把ip和端口以及发布的rpc服务注册
到注册中心，客户端或者rpc消费者从注册中心获取服务Provider列表，同时获取Provider提供的服务列表。另外客
户端还会监听注册中心的数据变化，获知server宕机或者服务不可用，将该Provider从客户端Provider缓存列表中
剔除，方便做容错和负载均衡。
```

#### 特性

```
一、	负载均衡
提供基于RoundRobin和随机方式的负载均衡
二、	高可用
Consumer会从注册中心获取到服务列表及该服务的提供者列表，如果某个提供者Provider网络异常或者宕机，
Consumer能马上感知到，加入不可用列表，如果从注册中心收到服务不可用会剔除缓存，不可用列表会重新尝试发起
连接，如果网络正常了会立即恢复。
三、	泛化调用
一般的rpc调用需要拿到服务提供方的业务api(interface class，入参class，返回值class打包到一个jar中，
依赖该jar)，如果使用泛型，只需要填写interface的name,版本，方法名称，参数名称，参数值，如果是对象，
将对象字段封装到一个Map中即可，无需依赖任何业务jar即可完成rpc调用。
四、限流降级
当访问流量过大时，需要通过服务端的限流降级优先保护服务端的服务，让客户端快速失败避免雪崩。
五、	Rpc上下文附件
Rpc调用方可以将需要传递的上下文信息填写到上下文中，而不是作为rpc的入参，这样Provider可以从上下文中获取
到掉用方的上下文信息。
六、	多种注册中心
Rpc 框架提供了zookeeper，etcd，redis pubsub（支持单个，或者sentinel集群模式）的注册中心，具备高可
用功能。
七、	实时动态监控
Provider提供了监控的api，监控可以使用该api加入到项目或者公司的监控平台。
八、可视化控制台管理
提供了基于web的可视化控制台查询服务提供方，服务调用方，在线配置机器权重等。
九、spring集成
只需简单配置就可将提供的服务或者依赖的服务集成到spring中。
十、	优雅停机
在服务发布和服务停止时会将服务优先级权重自动调整，避免在服务启动或者停止的时候对下游调用方造成影响。
```

#### 整体架构
![Alt text](http://img1.ph.126.net/oOtoQoleQRdL79YDFZCmqw==/6631377827978585092.png)

```
泛型：GenericService，Consumer不依赖Provider的api jar包即可完成remote api调用
监控：StatMonitor,consumer注册需要的远程服务StatMonitor，调用rpc获取监控数据
Webui:一个可视化rpc管理界面，https://github.com/lindzh/rpc-webui
RPC调用：使用jdk proxy封装发送tcp数据，并等待数据返回，完成RPC调用。
负载均衡：在集群模式下，同一个版本的Rpc服务在多台服务器上部署，Consumer发起rpc调用使用负载均衡。
自动容错：发现rpc provider不可用及时剔除，当可用时加入。
代理：rpcClient注册一个remote intface时会返回一个代理。
多注册中心：提供Zookeeper，etcd，redis等注册中心，并可以实现高可用。
```

#### 快速入门

> Provider

```
SimpleRpcServer rpcServer = new SimpleRpcServer();
rpcServer.setHost("192.168.132.87");
rpcServer.setPort(4321);
//将一个service暴露为rpc服务
rpcServer.register(LoginRpcService.class, new LoginRpcServiceImpl());
rpcServer.startService();
// Thread.currentThread().sleep(100000);//wait for call
rpcServer.stopService();
```

> Consumer

```
SimpleRpcClient rpcClient = new SimpleRpcClient();
rpcClient.setHost("192.168.132.87");
rpcClient.setPort(4321);
LoginRpcService loginRpcService = rpcClient.register(LoginRpcService.class);
rpcClient.startService();
boolean loginResult = loginRpcService.login("admin", "admin");
rpcClient.stopService();
```

#### 项目文档
https://github.com/lindzh/hasting/tree/master/docs

#### 如何加入项目
1. 有问题直接提issues : https://github.com/lindzh/hasting/issues
2. 改进项目请直接提PullRequest：https://github.com/lindzh/hasting/pulls

#### Lisence

```
(The MIT License)

Copyright (c) 2013-2015 github/lindzh

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
'Software'), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
```
