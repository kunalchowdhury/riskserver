package com.quantlogic.marketdata;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class MethodInvocationRegistry {
    private static final Map<Pair<String, String>, Triple<IntA, Method, Object[]>> invocationRegistry ;

    static  {
        invocationRegistry = new HashMap<>();
    }

    public static void register(String className, String methodName, IntA instance, Method methodinstance, Object[] args) {
        invocationRegistry.put(Pair.of(className, methodName), Triple.of(instance, methodinstance, args));
    }

    public static Triple<IntA, Method, Object[]> getMethodInfo(String className, String methodName) {
        return invocationRegistry.get(Pair.of(className, methodName));
    }

}
