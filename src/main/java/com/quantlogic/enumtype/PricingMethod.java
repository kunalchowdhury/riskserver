package com.quantlogic.enumtype;

import com.quantlogic.builder.pricingengine.BinomialCRRVanillaEngineBuilder;
import com.quantlogic.builder.pricingengine.BinomialJ4VanillaEngineBuilder;
import com.quantlogic.builder.pricingengine.BinomialLRVanillaEngineBuilder;
import com.quantlogic.builder.PricingEngineBuilder;

public enum PricingMethod {
    BinomialLeisenReimer(new BinomialLRVanillaEngineBuilder()),
    BinomialCoxRossRubinstein(new BinomialCRRVanillaEngineBuilder()),
    BinomialJoshi(new BinomialJ4VanillaEngineBuilder());

    private final PricingEngineBuilder pricingEngineBuilder;

    PricingMethod(PricingEngineBuilder pricingEngineBuilder) {
        this.pricingEngineBuilder = pricingEngineBuilder;
    }

    public PricingEngineBuilder getPricingEngineBuilder() {
        return pricingEngineBuilder;
    }
}
