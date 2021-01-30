package com.quantlogic.enumtype;

import com.quantlogic.builder.Builder;
import com.quantlogic.builder.yieldtermcurve.DiscountCurveBuilder;
import com.quantlogic.builder.yieldtermcurve.FlatForwardBuilder;
import org.quantlib.YieldTermStructure;

public enum YieldTermType {
    FlatForward(new FlatForwardBuilder()),
    DiscountCurve(new DiscountCurveBuilder());

    Builder<? extends YieldTermStructure> builder;
    YieldTermType(Builder<? extends YieldTermStructure> builder) {
        this.builder = builder;
    }

    public Builder<? extends YieldTermStructure> getBuilder() {
        return builder;
    }
}
