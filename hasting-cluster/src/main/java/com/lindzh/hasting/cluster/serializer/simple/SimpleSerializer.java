package com.lindzh.hasting.cluster.serializer.simple;

import com.lindzh.hasting.rpc.exception.RpcException;
import com.lindzh.hasting.rpc.serializer.RpcSerializer;

import java.io.*;

/**
 * Created by lin on 2016/12/2.
 * 跨语言序列化
 */
public class SimpleSerializer implements RpcSerializer{

    @Override
    public byte[] serialize(Object obj) {
        SimpleOutput out = new SimpleOutput(obj);
        try {
            byte[] result = out.writeObject();
            return result;
        } catch (IOException e) {
            throw new RpcException(e);
        }
    }

    @Override
    public Object deserialize(byte[] bytes) {
        SimpleInput input = new SimpleInput(bytes);
        try {
            Object result = input.readObject();
            return result;
        } catch (IOException e) {
            throw new RpcException(e);
        } catch (ClassNotFoundException e) {
            throw new RpcException(e);
        } catch (IllegalAccessException e) {
            throw new RpcException(e);
        } catch (InstantiationException e) {
            throw new RpcException(e);
        }
    }
}
