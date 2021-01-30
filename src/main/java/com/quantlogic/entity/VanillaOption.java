package com.quantlogic.entity;

import com.quantlogic.enumtype.DayCount;
import com.quantlogic.enumtype.ExcerciseType;
import com.quantlogic.enumtype.OptionType;

public class VanillaOption extends Instrument{

    private double strike ;
    private double underlying ;
    private double riskFreeRate ;
    private double dividendYield;
    private double volatility ;
    private long settlementDate;
    private long maturity;
    private DayCount dayCount;
    private OptionType optionType;
    private ExcerciseType excerciseType;
    private int version;
    private String tickerSymbol;
    private long insertTime;


    public VanillaOption(int id, String name) {
        super(id, name);
    }

    public double getStrike() {
        return strike;
    }

    public double getUnderlying() {
        return underlying;
    }

    public double getRiskFreeRate() {
        return riskFreeRate;
    }

    public double getDividendYield() {
        return dividendYield;
    }

    public double getVolatility() {
        return volatility;
    }

    public long getSettlementDate() {
        return settlementDate;
    }

    public long getMaturity() {
        return maturity;
    }

    public DayCount getDayCount() {
        return dayCount;
    }

    public OptionType getOptionType() {
        return optionType;
    }

    public ExcerciseType getExcerciseType() {
        return excerciseType;
    }

    public String getTickerSymbol() {
        return tickerSymbol;
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public long insertTime() {
        return insertTime;
    }


}
