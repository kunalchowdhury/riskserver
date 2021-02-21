package com.quantlogic.builder;

import com.quantlogic.builder.yieldtermcurve.YieldTermStructureBuilder;
import org.quantlib.BlackScholesMertonProcess;
import org.quantlib.QuoteHandle;
import org.quantlib.SimpleQuote;

public class BlackScholesMertonProcessBuilder implements Builder<BlackScholesMertonProcess> {
    private double underlyingPrice;
    private YieldTermStructureBuilder dividendBuilder;
    private YieldTermStructureBuilder yieldCurveBuilder;
    private BlackVolTermStructureBuilder volatilitySurfaceBuilder;

    public BlackScholesMertonProcessBuilder() {
    }

    public BlackScholesMertonProcessBuilder withUnderlyingPrice(double undPrc) {
        this.underlyingPrice = undPrc;
        return this;
    }

    public BlackScholesMertonProcessBuilder withDividendBuilder(YieldTermStructureBuilder yieldTermStructureBuilder){
        this.dividendBuilder = yieldTermStructureBuilder;
        return this;
    }

    public BlackScholesMertonProcessBuilder withYieldCurveBuilder(YieldTermStructureBuilder yieldTermStructureBuilder){
        this.yieldCurveBuilder = yieldTermStructureBuilder;
        return this;
    }


    public BlackScholesMertonProcessBuilder withVolatilitySurfaceBuilder(BlackVolTermStructureBuilder volatilitySurfaceBuilder){
        this.volatilitySurfaceBuilder = volatilitySurfaceBuilder;
        return this;
    }

    @Override
    public BlackScholesMertonProcess build() {
        return new BlackScholesMertonProcess(new QuoteHandle(new SimpleQuote(underlyingPrice)),
                dividendBuilder.build(),
                yieldCurveBuilder.build(),
                volatilitySurfaceBuilder.build());
    }
}
