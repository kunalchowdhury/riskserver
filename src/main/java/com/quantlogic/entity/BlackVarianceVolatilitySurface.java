package com.quantlogic.entity;

import com.quantlogic.annotation.BaseEntity;
import com.quantlogic.enumtype.DayCount;
import com.quantlogic.enumtype.USMarketType;

import java.util.Arrays;

@BaseEntity
public class BlackVarianceVolatilitySurface extends Volatility{


    private Long valuationDate ;
    private USMarketType calendar;
    private Long[] expirations;
    private Double[] strikes;
    private DayCount curDayCounter;
    private Double[][] vols;

    public BlackVarianceVolatilitySurface(int id, String name, int version, Long insertTime) {
        super(id, name, version, insertTime);
    }


    public BlackVarianceVolatilitySurface(int id, String name, int version, Long insertTime, Long valuationDate,
                                          USMarketType calendar, Long[] expirations, Double[] strikes,
                                          DayCount curDayCounter, Double[][] vols) {
        super(id, name, version, insertTime);
        this.valuationDate = valuationDate;
        this.calendar = calendar;
        this.expirations = expirations;
        this.strikes = strikes;
        this.curDayCounter = curDayCounter;
        this.vols = vols;
    }

    public Long getValuationDate() {
        return valuationDate;
    }

    public USMarketType getCalendar() {
        return calendar;
    }

    public void setValuationDate(Long valuationDate) {
        this.valuationDate = valuationDate;
    }

    public Long[] getExpirations() {
        return expirations;
    }

    public DayCount getCurDayCounter() {
        return curDayCounter;
    }

    public void setExpirations(Long[] expirations) {
        this.expirations = expirations;
    }

    public Double[] getStrikes() {
        return strikes;
    }

    public void setStrikes(Double[] strikes) {
        this.strikes = strikes;
    }

    public Double[][] getVols() {
        return vols;
    }

    public void setVols(Double[][] vols) {
        this.vols = vols;
    }

    public void setCalendar(USMarketType calendar) {
        this.calendar = calendar;
    }

    public void setArrayStrikes(int i, double strike) {
        this.strikes[i] = strike;
    }

    public void setCurDayCounter(DayCount curDayCounter) {
        this.curDayCounter = curDayCounter;
    }

    public void setArrayVols(int i, int j, double vol) {
        this.vols[i][j] = vol;
    }


    @Override
    public String toString() {
        return "BlackVarianceVolatilitySurface{" +
                "valuationDate=" + valuationDate +
                ", calendar=" + calendar +
                ", expirations=" + Arrays.toString(expirations) +
                ", strikes=" + Arrays.toString(strikes) +
                ", curDayCounter=" + curDayCounter +
                ", vols=" + Arrays.toString(vols) +
                '}';
    }
}
