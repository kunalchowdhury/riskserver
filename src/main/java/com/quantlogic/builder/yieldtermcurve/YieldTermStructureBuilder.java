package com.quantlogic.builder.yieldtermcurve;

import com.quantlogic.builder.Builder;
import com.quantlogic.enumtype.DayCount;
import com.quantlogic.enumtype.YieldTermType;
import org.quantlib.YieldTermStructure;
import org.quantlib.YieldTermStructureHandle;

import java.time.Month;

public class YieldTermStructureBuilder implements Builder<YieldTermStructureHandle> {
    private Builder<?> builder;
    private YieldTermType yieldTermType ;

    public YieldTermStructureBuilder(){}

    public YieldTermStructureBuilder withFlatForwardBuilder(FlatForwardBuilder flatForwardBuilder){
        this.builder = flatForwardBuilder;
        return this;
    }

    public YieldTermStructureBuilder withType(YieldTermType yieldTermType) {
        this.yieldTermType = yieldTermType;
        switch (yieldTermType){
            case FlatForward:
                builder = new FlatForwardBuilder();
                break;
            case DiscountCurve:
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + yieldTermType);
        }
        return this;
    }

    public YieldTermStructureBuilder withRiskFreeRate(double riskFreeRate){
        switch (yieldTermType) {
            case FlatForward:
                ((FlatForwardBuilder) builder).withRiskFreeRate(riskFreeRate);
                break;
            case DiscountCurve:
                break;
        }
        return this;
    }

    public YieldTermStructureBuilder withSettlementDay(int day){
        switch (yieldTermType) {
            case FlatForward:
                ((FlatForwardBuilder) builder).withSettlementDay(day);
                break;
            case DiscountCurve:
                break;
        }
        return this;
    }

    public YieldTermStructureBuilder withSettlementMonth(Month month){
        switch (yieldTermType) {
            case FlatForward:
                ((FlatForwardBuilder) builder).withSettlementMonth(month);
                break;
            case DiscountCurve:
                break;
        }
        return this;
    }

    public YieldTermStructureBuilder withSettlementYear(int year){
        switch (yieldTermType) {
            case FlatForward:
                ((FlatForwardBuilder) builder).withSettlementYear(year);
                break;
            case DiscountCurve:
                break;
        }
        return this;
    }

    public YieldTermStructureBuilder withDayCount(DayCount dayCount){
        switch (yieldTermType) {
            case FlatForward:
                ((FlatForwardBuilder) builder).withDayCount(dayCount);
                break;
            case DiscountCurve:
                break;
        }
        return this;
    }

    public YieldTermStructureHandle build(){
        return new YieldTermStructureHandle((YieldTermStructure) builder.build());
    }


}
