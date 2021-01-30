package com.quantlogic.builder.pricingengine;

import com.quantlogic.builder.PricingEngineBuilder;
import org.quantlib.BinomialLRVanillaEngine;

public class BinomialLRVanillaEngineBuilder extends PricingEngineBuilder {
    @Override
    public BinomialLRVanillaEngine build() {
        return new BinomialLRVanillaEngine(process, timeSteps);
    }
}
