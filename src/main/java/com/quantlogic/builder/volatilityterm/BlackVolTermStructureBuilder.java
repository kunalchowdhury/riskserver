package com.quantlogic.builder.volatilityterm;

import com.quantlogic.builder.Builder;
import com.quantlogic.builder.VolatilitySurfaceBuilder;
import org.quantlib.BlackVolTermStructureHandle;

public class BlackVolTermStructureBuilder implements Builder<BlackVolTermStructureHandle> {
    private VolatilitySurfaceBuilder volatilitySurfaceBuilder;

    public BlackVolTermStructureBuilder() {
    }

    public BlackVolTermStructureBuilder withVolSurfaceBuilder(VolatilitySurfaceBuilder volSurfaceBuilder) {
        this.volatilitySurfaceBuilder = volSurfaceBuilder;
        return this;
    }


    @Override
    public BlackVolTermStructureHandle build() {
        return new BlackVolTermStructureHandle(volatilitySurfaceBuilder.build());
    }
}
