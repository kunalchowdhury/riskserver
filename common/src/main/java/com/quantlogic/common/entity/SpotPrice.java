package com.quantlogic.common.entity;

import com.quantlogic.dto.SpotPriceDTO;

import java.util.Objects;

public class SpotPrice implements NamedTimedEntity {
    private String ticker;
    private double mid;
    private double hi;
    private double lo;
    private double open;
    private double close;
    private long snapshotTime;
    private String name;
    private int version;

    public SpotPrice() {
    }

    public SpotPrice(SpotPriceDTO spotPriceDTO) {
        this(spotPriceDTO.getTicker(), spotPriceDTO.getMid(), spotPriceDTO.getHi(), spotPriceDTO.getLo(),
                spotPriceDTO.getOpen(), spotPriceDTO.getClose(), spotPriceDTO.getName(), spotPriceDTO.getVersion());
    }

    public SpotPrice(String ticker, double mid, double hi, double lo, double open, double close, String name, int version) {
        this.ticker = ticker;
        this.mid = mid;
        this.hi = hi;
        this.lo = lo;
        this.open = open;
        this.close = close;
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

    public void setSnapshotTime(long snapshotTime) {
        this.snapshotTime = snapshotTime;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public long getSnapshotTime() {
        return this.snapshotTime;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpotPrice spotPrice = (SpotPrice) o;
        return Double.compare(spotPrice.mid, mid) == 0 && Double.compare(spotPrice.hi, hi) == 0
                && Double.compare(spotPrice.lo, lo) == 0 && Double.compare(spotPrice.open, open) == 0
                && Double.compare(spotPrice.close, close) == 0 && snapshotTime == spotPrice.snapshotTime
                && version == spotPrice.version && ticker.equals(spotPrice.ticker) && name.equals(spotPrice.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticker, mid, hi, lo, open, close, snapshotTime, name, version);
    }


    @Override
    public String toString() {
        return "SpotPrice{" +
                "ticker='" + ticker + '\'' +
                ", mid=" + mid +
                ", hi=" + hi +
                ", lo=" + lo +
                ", open=" + open +
                ", close=" + close +
                ", snapshotTime=" + snapshotTime +
                ", name='" + name + '\'' +
                ", version=" + version +
                '}';
    }
}
