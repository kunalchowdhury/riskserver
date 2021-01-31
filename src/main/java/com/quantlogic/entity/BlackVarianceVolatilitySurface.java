package com.quantlogic.entity;

import com.quantlogic.enumtype.DayCount;
import com.quantlogic.enumtype.USMarketType;

public class BlackVarianceVolatilitySurface extends Volatility{


    private long valuationDate ;
    private USMarketType calendar;
    private long[] expirations;
    private double[] strikes;
    private DayCount curDayCounter;
    private double[][] vols;

    public BlackVarianceVolatilitySurface(int id, String name, int version, long insertTime) {
        super(id, name, version, insertTime);
    }


    public BlackVarianceVolatilitySurface(int id, String name, int version, long insertTime, long valuationDate,
                                          USMarketType calendar, long[] expirations, double[] strikes,
                                          DayCount curDayCounter, double[][] vols) {
        super(id, name, version, insertTime);
        this.valuationDate = valuationDate;
        this.calendar = calendar;
        this.expirations = expirations;
        this.strikes = strikes;
        this.curDayCounter = curDayCounter;
        this.vols = vols;
    }

    public long getValuationDate() {
        return valuationDate;
    }

    public USMarketType getCalendar() {
        return calendar;
    }

    public long[] getExpirations() {
        return expirations;
    }

    public double[] getStrikes() {
        return strikes;
    }

    public DayCount getCurDayCounter() {
        return curDayCounter;
    }

    public double[][] getVols() {
        return vols;
    }
}
