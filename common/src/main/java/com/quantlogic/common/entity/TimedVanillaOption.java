package com.quantlogic.common.entity;

import com.quantlogic.dto.VanillaOptionDTO;

import java.util.Objects;

public class TimedVanillaOption implements NamedTimedEntity {
    private double strike ;
    private String underlying ;
    private double riskFreeRate ;
    private double dividendYield;
    private double volatility ;
    private long settlementDate;
    private long maturity;
    private byte dayCount;
    private byte optionType;
    private byte excerciseType;
    private String tickerSymbol;
    private int version;
    private int shardId;
    private long snapshotTime;
    private String name;

    public TimedVanillaOption(VanillaOptionDTO vanillaOptionDTO) {
        this(vanillaOptionDTO.getStrike(), vanillaOptionDTO.getUnderlying(), vanillaOptionDTO.getRiskFreeRate(),
                vanillaOptionDTO.getDividendYield(), vanillaOptionDTO.getVolatility(),
                vanillaOptionDTO.getSettlementDate(), vanillaOptionDTO.getMaturity(),
                vanillaOptionDTO.getDayCount(), vanillaOptionDTO.getOptionType(),
                vanillaOptionDTO.getExcerciseType(), vanillaOptionDTO.getTickerSymbol(),
                vanillaOptionDTO.getVersion(), vanillaOptionDTO.getShardId(), vanillaOptionDTO.getName());
    }

    public TimedVanillaOption(double strike, String underlying, double riskFreeRate, double dividendYield,
                              double volatility, long settlementDate, long maturity, byte dayCount, byte optionType,
                              byte excerciseType, String tickerSymbol, int version, int shardId, String name) {
        this.strike = strike;
        this.underlying = underlying;
        this.riskFreeRate = riskFreeRate;
        this.dividendYield = dividendYield;
        this.volatility = volatility;
        this.settlementDate = settlementDate;
        this.maturity = maturity;
        this.dayCount = dayCount;
        this.optionType = optionType;
        this.excerciseType = excerciseType;
        this.tickerSymbol = tickerSymbol;
        this.version = version;
        this.shardId = shardId;
        this.name = name;
    }

    public void setSnapshotTime(long snapshotTime) {
        this.snapshotTime = snapshotTime;
    }

    public double getStrike() {
        return strike;
    }

    public void setStrike(double strike) {
        this.strike = strike;
    }

    public String getUnderlying() {
        return underlying;
    }

    public void setUnderlying(String underlying) {
        this.underlying = underlying;
    }

    public double getRiskFreeRate() {
        return riskFreeRate;
    }

    public void setRiskFreeRate(double riskFreeRate) {
        this.riskFreeRate = riskFreeRate;
    }

    public double getDividendYield() {
        return dividendYield;
    }

    public void setDividendYield(double dividendYield) {
        this.dividendYield = dividendYield;
    }

    public double getVolatility() {
        return volatility;
    }

    public void setVolatility(double volatility) {
        this.volatility = volatility;
    }

    public long getSettlementDate() {
        return settlementDate;
    }

    public void setSettlementDate(long settlementDate) {
        this.settlementDate = settlementDate;
    }

    public long getMaturity() {
        return maturity;
    }

    public void setMaturity(long maturity) {
        this.maturity = maturity;
    }

    public byte getDayCount() {
        return dayCount;
    }

    public void setDayCount(byte dayCount) {
        this.dayCount = dayCount;
    }

    public byte getOptionType() {
        return optionType;
    }

    public void setOptionType(byte optionType) {
        this.optionType = optionType;
    }

    public byte getExcerciseType() {
        return excerciseType;
    }

    public void setExcerciseType(byte excerciseType) {
        this.excerciseType = excerciseType;
    }

    public String getTickerSymbol() {
        return tickerSymbol;
    }

    public void setTickerSymbol(String tickerSymbol) {
        this.tickerSymbol = tickerSymbol;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getShardId() {
        return shardId;
    }

    public void setShardId(int shardId) {
        this.shardId = shardId;
    }

    public long getSnapshotTime() {
        return snapshotTime;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimedVanillaOption that = (TimedVanillaOption) o;
        return Double.compare(that.strike, strike) == 0 && Double.compare(that.riskFreeRate, riskFreeRate) == 0
                && Double.compare(that.dividendYield, dividendYield) == 0
                && Double.compare(that.volatility, volatility) == 0
                && settlementDate == that.settlementDate && maturity == that.maturity
                && dayCount == that.dayCount && optionType == that.optionType
                && excerciseType == that.excerciseType && version == that.version && shardId == that.shardId
                && snapshotTime == that.snapshotTime && underlying.equals(that.underlying)
                && tickerSymbol.equals(that.tickerSymbol) && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(strike, underlying, riskFreeRate, dividendYield, volatility, settlementDate, maturity,
                dayCount, optionType, excerciseType, tickerSymbol, version, shardId, snapshotTime, name);
    }
}
