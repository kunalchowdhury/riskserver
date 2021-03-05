package com.quantlogic.marketdata;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class DynamicInvocationHandler implements InvocationHandler {
    private A a;

    public DynamicInvocationHandler(A a) {
        this.a = a;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("Start");
        MethodInvocationRegistry.register(a.getClass().getCanonicalName(), "fun", a, method, args );
        method.invoke(a, args);
        System.out.println("end");
        return 1;
    }
}
