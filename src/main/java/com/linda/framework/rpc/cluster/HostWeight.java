package com.linda.framework.rpc.cluster;

/**
 * Created by lin on 2016/12/10.
 */
public class HostWeight {

    private String host;

    private int port;

    private int weight;

    public String getKey(){
        return host+":"+port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
