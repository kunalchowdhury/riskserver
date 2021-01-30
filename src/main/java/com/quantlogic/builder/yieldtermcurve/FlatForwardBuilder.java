package com.quantlogic.builder.yieldtermcurve;

import com.quantlogic.builder.Builder;
import com.quantlogic.enumtype.DayCount;
import org.quantlib.*;

import java.time.Month;

public class FlatForwardBuilder implements Builder<FlatForward> {
    private int day;
    private Month month;
    private int year;
    private double riskFreeRate;
    private DayCounter dayCounter;

    public FlatForwardBuilder() { }

    public FlatForwardBuilder withRiskFreeRate(double riskFreeRate){
        this.riskFreeRate = riskFreeRate;
        return this;
    }

    public FlatForwardBuilder withSettlementDay(int day){
        this.day = day;
        return this;
    }

    public FlatForwardBuilder withSettlementMonth(Month month){
        this.month = month;
        return this;
    }

    public FlatForwardBuilder withSettlementYear(int year){
        this.year = year;
        return this;
    }

    public FlatForwardBuilder withDayCount(DayCount dayCount){
        switch (dayCount){
            case ACTUAL_365_FIXED:
                this.dayCounter = new Actual365Fixed();
                break;
            case ACTUAL_360:
                this.dayCounter = new Actual360();
            default:
                throw new IllegalStateException("Unexpected value: " + dayCount);
        }
        return this;
    }

    public FlatForward build(){
        Date settlementDate = new Date(this.day, com.quantlogic.enumtype.Month.getQuantLibMonth(month), this.year);
        return new FlatForward(settlementDate, riskFreeRate, dayCounter);
    }

}
