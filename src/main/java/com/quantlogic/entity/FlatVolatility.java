package com.quantlogic.entity;

import com.quantlogic.annotation.BaseEntity;
import org.quantlib.Calendar;
import org.quantlib.Date;
import org.quantlib.DayCounter;

@BaseEntity
public class FlatVolatility extends Volatility{
    private Date settlementDate;
    private Calendar calendar;
    private DayCounter dayCounter;
    private double volatility;

    public FlatVolatility(int id, String name, int version, long insertTime) {
        super(id, name, version, insertTime);
    }

    public FlatVolatility(int id, String name, int version, long insertTime, Date settlementDate, Calendar calendar,
                          DayCounter dayCounter, double volatility) {
        super(id, name, version, insertTime);
        this.settlementDate = settlementDate;
        this.calendar = calendar;
        this.dayCounter = dayCounter;
        this.volatility = volatility;
    }

    public Date getSettlementDate() {
        return settlementDate;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public DayCounter getDayCounter() {
        return dayCounter;
    }

    public double getVolatility() {
        return volatility;
    }
}
