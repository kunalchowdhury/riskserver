package com.quantlogic.builder;

import org.quantlib.BlackScholesMertonProcess;
import org.quantlib.PricingEngine;

public abstract class PricingEngineBuilder implements Builder<PricingEngine> {
    protected BlackScholesMertonProcess process;
    protected int timeSteps;

    public PricingEngineBuilder withProcess(BlackScholesMertonProcess process){
        this.process = process;
        return this;
    }

    public PricingEngineBuilder withSteps(int timeSteps){
        this.timeSteps = timeSteps;
        return this;
    }
}
