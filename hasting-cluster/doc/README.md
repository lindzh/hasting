##RPC Cluster adminstration support

provide etcd,zookeeper and redis for rpc service administration and rpc service notify

>Zookeeper registry use example
##server example
```java
ZkRpcServer rpcServer = new ZkRpcServer();
//init zk
rpcServer.setConnectString("192.168.139.129:2215,192.168.139.129:2225,192.168.139.129:2235");
rpcServer.setNamespace("myrpc");
//init provider protocol
rpcServer.setHost("127.0.0.1");
rpcServer.setPort(3333);
//regist a rpc impl for remote call
rpcServer.register(HelloRpcService.class, new HelloRpcServiceImpl());
//start rpc service
rpcServer.startService();
```

####client use
```java
ZkRpcClient client = new ZkRpcClient();
client.setConnectString("192.168.139.129:2215,192.168.139.129:2225,192.168.139.129:2235");
client.setNamespace("myrpc");
client.startService();

HelloRpcService rpcService = client.register(HelloRpcService.class);
//call remote api
rpcService.sayHello("this is rpc etcd test", 10);
```

>Etcd registry use example
##server example
```java
EtcdRpcServer rpcServer = new EtcdRpcServer();
###server impl
//init etcd
rpcServer.setEtcdUrl("http://192.168.139.129:2911");
rpcServer.setNamespace("lindezhi");
//init provider protocol
rpcServer.setHost("127.0.0.1");
rpcServer.setPort(3332);
//register a rpc impl as remote service
rpcServer.register(HelloRpcService.class, new HelloRpcServiceImpl());
//start rpc
rpcServer.startService();
```

##client example
```java
EtcdRpcClient client = new EtcdRpcClient();
client.setEtcdUrl("http://192.168.139.129:2911");
client.setNamespace("lindezhi");
client.startService();
rpcService.sayHello("this is rpc etcd test", 10);
```

>Redis registry use example
##server example use redis pub/sub for admin and notify
```java
RedisRpcServer rpcServer = new RedisRpcServer();
rpcServer.setHost("127.0.0.1");
rpcServer.setPort(3333);
rpcServer.setRedisHost("192.168.139.129");
rpcServer.setRedisPort(7770);
rpcServer.register(HelloRpcService.class, new HelloRpcServiceImpl());
rpcServer.startService();
```

###client example use redis pub/sub
```java
RedisRpcClient rpcClient = new RedisRpcClient();
rpcClient.setRedisHost("192.168.139.129");
rpcClient.setRedisPort(7770);
rpcClient.startService();
HelloRpcService rpcService = client.register(HelloRpcService.class);
rpcService.sayHello("this is rpc etcd test", 10);
```



