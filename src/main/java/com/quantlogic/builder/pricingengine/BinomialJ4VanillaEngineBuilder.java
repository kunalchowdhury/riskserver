package com.quantlogic.builder.pricingengine;

import com.quantlogic.builder.PricingEngineBuilder;
import org.quantlib.BinomialCRRVanillaEngine;
import org.quantlib.BinomialJ4VanillaEngine;

public class BinomialJ4VanillaEngineBuilder extends PricingEngineBuilder {
    @Override
    public BinomialJ4VanillaEngine build() {
        return new BinomialJ4VanillaEngine(process, timeSteps);
    }
}
