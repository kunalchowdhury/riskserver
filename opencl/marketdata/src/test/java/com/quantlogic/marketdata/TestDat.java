package com.quantlogic.marketdata;

import org.apache.commons.lang3.tuple.Triple;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicLongArray;

public class TestDat {

    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException {

        AtomicLongArray ar = new AtomicLongArray(12);

        IntA proxyInstance = (IntA) Proxy.newProxyInstance(
                DynamicInvocationHandler.class.getClassLoader(),
                new Class[] { IntA.class },
                new DynamicInvocationHandler(new A()));

        proxyInstance.fun();
        Triple<IntA, Method, Object[]> fun = MethodInvocationRegistry.getMethodInfo(A.class.getCanonicalName(), "fun");
        fun.getMiddle().invoke(fun.getLeft(), fun.getRight());


    }
}
