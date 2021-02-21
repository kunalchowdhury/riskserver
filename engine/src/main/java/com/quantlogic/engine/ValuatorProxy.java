package com.quantlogic.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ValuatorProxy implements InvocationHandler {
    private final ValuationExecutor valuationExecutor;
    private final ValuationOrchestrator valuationOrchestrator;
    private static final Logger LOGGER = LoggerFactory.getLogger(ValuatorProxy.class);

    public ValuatorProxy(ValuationExecutor valuationExecutor, ValuationOrchestrator valuationOrchestrator) {
        this.valuationExecutor = valuationExecutor;
        this.valuationOrchestrator = valuationOrchestrator;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        valuationOrchestrator.registerValuator(valuationExecutor);
        long start = System.nanoTime();
        Object res = method.invoke(valuationExecutor);
        LOGGER.info("Total Valuation Time = {} secs", (System.nanoTime() - start)/Math.pow(10, 9));
        return res;
    }
}
