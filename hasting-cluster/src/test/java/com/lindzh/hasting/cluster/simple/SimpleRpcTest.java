package com.lindzh.hasting.cluster.simple;

import com.lindzh.hasting.rpc.client.SimpleRpcClient;
import com.lindzh.hasting.cluster.HelloRpcService;

/**
 * Created by lin on 2016/12/9.
 */
public class SimpleRpcTest {

    public static void main(String[] args) {
        SimpleRpcClient client = new SimpleRpcClient();
        client.setHost("127.0.0.1");
        client.setPort(3333);
        client.setApplication("test");

        HelloRpcService hello = client.register(HelloRpcService.class,null,"hello");

        client.startService();

        hello.sayHello("664567",66);

        String hello1 = hello.getHello();

        System.out.println("hahahah----------");
    }
}
