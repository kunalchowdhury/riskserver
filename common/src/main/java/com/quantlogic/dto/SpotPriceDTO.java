package com.quantlogic.dto;

import java.util.Objects;

public class SpotPriceDTO implements DTOEntity{
    private String ticker;
    private double mid;
    private double hi;
    private double lo;
    private double open;
    private double close;
    private int version;

    public SpotPriceDTO(String ticker, double mid, double hi, double lo, double open, double close, int version) {
        this.ticker = ticker;
        this.mid = mid;
        this.hi = hi;
        this.lo = lo;
        this.open = open;
        this.close = close;
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

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String getName() {
        return this.ticker;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpotPriceDTO that = (SpotPriceDTO) o;
        return Double.compare(that.mid, mid) == 0 && Double.compare(that.hi, hi) == 0 && Double.compare(that.lo, lo) == 0
                && Double.compare(that.open, open) == 0 && Double.compare(that.close, close) == 0
                && version == that.version && ticker.equals(that.ticker);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticker, mid, hi, lo, open, close, version);
    }

    @Override
    public String toString() {
        return "SpotPriceDTO{" +
                "ticker='" + ticker + '\'' +
                ", mid=" + mid +
                ", hi=" + hi +
                ", lo=" + lo +
                ", open=" + open +
                ", close=" + close +
                ", version=" + version +
                '}';
    }
}
