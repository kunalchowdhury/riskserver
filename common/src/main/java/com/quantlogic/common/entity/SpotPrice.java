package com.quantlogic.common.entity;

import java.util.Objects;

public class SpotPrice implements NamedTimedEntity {
    private String ticker;
    private double mid;
    private double hi;
    private double lo;
    private double open;
    private double close;
    private long snapshot;
    private String name;
    private long version;


    public SpotPrice(String ticker, double mid, double hi, double lo, double open, double close, long snapshot, String name, long version) {
        this.ticker = ticker;
        this.mid = mid;
        this.hi = hi;
        this.lo = lo;
        this.open = open;
        this.close = close;
        this.snapshot = snapshot;
        this.name = name;
        this.version = version;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public double getMid() {
        return mid;
    }

    public void setMid(double mid) {
        this.mid = mid;
    }

    public double getHi() {
        return hi;
    }

    public void setHi(double hi) {
        this.hi = hi;
    }

    public double getLo() {
        return lo;
    }

    public void setLo(double lo) {
        this.lo = lo;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public long getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(long snapshot) {
        this.snapshot = snapshot;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public long getSnapshotTime() {
        return this.snapshot;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpotPrice spotPrice = (SpotPrice) o;
        return Double.compare(spotPrice.mid, mid) == 0 && Double.compare(spotPrice.hi, hi) == 0
                && Double.compare(spotPrice.lo, lo) == 0 && Double.compare(spotPrice.open, open) == 0
                && Double.compare(spotPrice.close, close) == 0 && snapshot == spotPrice.snapshot
                && version == spotPrice.version && ticker.equals(spotPrice.ticker) && name.equals(spotPrice.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticker, mid, hi, lo, open, close, snapshot, name, version);
    }
}
