package com.lindzh.hasting.cluster.serializer.simple;

/**
 * Created by lin on 2016/12/5.
 */
public class SimpleType {

    private byte type;

    private String name;

    public SimpleType(){

    }

    public SimpleType(byte type,String name){
        this.type = type;
        this.name = name;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SimpleType copy(){
        return new SimpleType(this.type,this.name);
    }
}
