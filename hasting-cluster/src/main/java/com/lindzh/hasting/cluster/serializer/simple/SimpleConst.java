package com.lindzh.hasting.cluster.serializer.simple;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lin on 2016/12/2.
 */
public class SimpleConst {

    //==========================基本类型=======================

    static final byte NotNull = -128;

    static final byte intType = 1;

    static final byte IntegerType = 2;

    static final byte longType = 3;

    static final byte LongType = 4;

    static final byte floatType = 5;

    static final byte FloatType = 6;

    static final byte doubleType = 7;

    static final byte DoubleType = 8;

    static final byte shortType = 9;

    static final byte ShortType = 10;

    static final byte booleanType = 11;

    static final byte BooleanType = 12;

    static final byte byteType = 13;

    static final byte ByteType = 14;

    static final byte charType = 15;

    static final byte CharacterType = 16;

    static final byte StringType = 17;

    //============================集合类型定义=========================

    static final byte ArrayType = 100;

    static final byte MapType = 18;

    static final byte ListType = 19;

    static final byte SetType = 20;


    //===========================任意类型定义=====主要针对集合操作来说==集合里面元素的类型由值确定======================
    static final byte AnyType = 21;

    //============================Object类型定义------------------------

    static final byte ObjectType = 22;

    static final Map<Byte,SimpleType> typeMap = new HashMap();

    static {
        typeMap.put(intType,new SimpleType(intType,"int"));
        typeMap.put(shortType,new SimpleType(shortType,"short"));
        typeMap.put(longType,new SimpleType(longType,"long"));
        typeMap.put(floatType,new SimpleType(floatType,"float"));
        typeMap.put(doubleType,new SimpleType(doubleType,"double"));
        typeMap.put(booleanType,new SimpleType(booleanType,"boolean"));
        typeMap.put(byteType,new SimpleType(byteType,"byte"));
        typeMap.put(charType,new SimpleType(charType,"char"));

        typeMap.put(IntegerType,new SimpleType(IntegerType,"Integer"));
        typeMap.put(LongType,new SimpleType(LongType,"Long"));
        typeMap.put(ShortType,new SimpleType(shortType,"Short"));
        typeMap.put(FloatType,new SimpleType(FloatType,"Float"));
        typeMap.put(DoubleType,new SimpleType(DoubleType,"Double"));
        typeMap.put(BooleanType,new SimpleType(BooleanType,"Boolean"));
        typeMap.put(ByteType,new SimpleType(ByteType,"Byte"));
        typeMap.put(CharacterType,new SimpleType(CharacterType,"Character"));

        typeMap.put(StringType,new SimpleType(StringType,"String"));

        //指定对象
        typeMap.put(ObjectType,new SimpleType(ObjectType,"any"));

        typeMap.put(AnyType,new SimpleType(AnyType,"Object"));
    }
}
