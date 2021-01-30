package com.quantlogic.builder.pricingengine;

import com.quantlogic.builder.PricingEngineBuilder;
import org.quantlib.BinomialCRRVanillaEngine;

public class BinomialCRRVanillaEngineBuilder extends PricingEngineBuilder {
    @Override
    public BinomialCRRVanillaEngine build() {
        return new BinomialCRRVanillaEngine(process, timeSteps);
    }
}
