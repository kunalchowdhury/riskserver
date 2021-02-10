package com.quantlogic.common.entity;

import java.util.Arrays;
import java.util.Objects;

public class TimedBlackVarianceVolatility implements NamedTimedEntity {
    private long valuationDate ;
    private byte calendar;
    private long[] expirations;
    private double[] strikes;
    private byte curDayCounter;
    private double[][] vols;
    private long version;
    private int shardId;
    private long snapshotTime;
    private String name;


    public TimedBlackVarianceVolatility(long valuationDate, byte calendar, long[] expirations,
                                        double[] strikes, byte curDayCounter, double[][] vols,
                                        long version, int shardId, String name) {
        this.valuationDate = valuationDate;
        this.calendar = calendar;
        this.expirations = expirations;
        this.strikes = strikes;
        this.curDayCounter = curDayCounter;
        this.vols = vols;
        this.version = version;
        this.shardId = shardId;
        this.name = name;
    }

    public long getValuationDate() {
        return valuationDate;
    }

    public void setValuationDate(long valuationDate) {
        this.valuationDate = valuationDate;
    }

    public byte getCalendar() {
        return calendar;
    }

    public void setCalendar(byte calendar) {
        this.calendar = calendar;
    }

    public long[] getExpirations() {
        return expirations;
    }

    public void setExpirations(long[] expirations) {
        this.expirations = expirations;
    }

    public double[] getStrikes() {
        return strikes;
    }

    public void setStrikes(double[] strikes) {
        this.strikes = strikes;
    }

    public byte getCurDayCounter() {
        return curDayCounter;
    }

    public void setCurDayCounter(byte curDayCounter) {
        this.curDayCounter = curDayCounter;
    }

    public double[][] getVols() {
        return vols;
    }

    public void setVols(double[][] vols) {
        this.vols = vols;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public int getShardId() {
        return shardId;
    }

    public void setShardId(int shardId) {
        this.shardId = shardId;
    }

    @Override
    public long getSnapshotTime() {
        return snapshotTime;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setSnapshotTime(long snapshotTime) {
        this.snapshotTime = snapshotTime;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimedBlackVarianceVolatility that = (TimedBlackVarianceVolatility) o;
        return valuationDate == that.valuationDate && calendar == that.calendar && curDayCounter == that.curDayCounter
                && version == that.version && shardId == that.shardId && snapshotTime == that.snapshotTime
                && Arrays.equals(expirations, that.expirations) && Arrays.equals(strikes, that.strikes)
                && Arrays.deepEquals(vols, that.vols) && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(valuationDate, calendar, curDayCounter, version, shardId, snapshotTime, name);
        result = 31 * result + Arrays.hashCode(expirations);
        result = 31 * result + Arrays.hashCode(strikes);
        result = 31 * result + Arrays.hashCode(vols);
        return result;
    }
}
