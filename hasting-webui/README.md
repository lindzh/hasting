# Hasting可视化管控组件

> you can see your services on web provided by rpc redis,zookeeper,etcd or even a simple node provider

#### service list
![service  list](http://img1.ph.126.net/rK4wt_--QIxjLcCa0Au4uw==/6630535602071912419.png)

#### providid service host list
![service host list](http://img2.ph.126.net/McBmMs0vB5BoTHk9olEv-w==/6619407444887116577.png)

#### host list
![host list](http://img0.ph.126.net/WNv5sLyPBX5X-8v04ui04g==/6619279901538292740.png)

#### host services list
![host services](http://img2.ph.126.net/QhAM9bCl_BUU3ruVkVle-w==/6619279901538292739.png)

#### How to config and start?
```
webui.json
[
    {
        "namespace": "default",
        "protocol": "redis",
        "etcdUrl": null,
        "redisHost": "192.168.139.129",
        "redisPort": 7770,
        "sentinelMaster": null,
        "sentinels": null,
        "providerHost": null,
        "providerPort": 0,
        "zkConnectionString": null
    }
]
namespace:for different cluster
protocol:redis,etcd,zookeeper,simple,for cluster message notify and store
etcdUrl:when protocol is etcd use
redisHost:when protocol is redis use and use a single redis node
redisPort:when protocol is redis use and use a single redis node
sentinelMaster:when protocol is redis use and use redis sentinel for fail over
sentinels:when protocol is redis use and use redis sentinel for fail over
providerHost:when protocol is simple single node provider use
providerPort:when protocol is simple single node provider use
zkConnectionString:when protocol is zookeeper use
```
#### How to start service
execute :  maven clean install war:inplace -Dmaven.test.skip
then config tomcat to webapp,start tomcat