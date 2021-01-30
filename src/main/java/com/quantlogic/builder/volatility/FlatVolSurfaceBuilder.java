package com.quantlogic.builder.volatility;

import com.quantlogic.builder.VolatilitySurfaceBuilder;
import com.quantlogic.util.DateUtil;
import org.quantlib.BlackConstantVol;
import org.quantlib.Calendar;
import org.quantlib.Date;
import org.quantlib.DayCounter;

public class FlatVolSurfaceBuilder extends VolatilitySurfaceBuilder {
    private Date settlementDate;
    private Calendar calendar;
    private DayCounter dayCounter;
    private double volatility;

    public FlatVolSurfaceBuilder() {
    }

    public FlatVolSurfaceBuilder withSettlementDate(long settlementDateInMillis){
        this.settlementDate = DateUtil.fromEpochMillis(settlementDateInMillis);
        return this;
    }

    public FlatVolSurfaceBuilder withCalendar(long settlementDateInMillis){
        this.settlementDate = DateUtil.fromEpochMillis(settlementDateInMillis);
        return this;
    }



    @Override
    public BlackConstantVol build() {
        return null;
    }
}
