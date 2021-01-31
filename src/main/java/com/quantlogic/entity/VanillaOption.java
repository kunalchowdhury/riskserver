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
    private String tickerSymbol;



    public VanillaOption(int id, String name, int version, long insertTime) {
        super(id, name, version, insertTime);
    }

    public VanillaOption(int id, String name, double strike, double underlying, double riskFreeRate, double dividendYield,
                         double volatility, long settlementDate, long maturity, DayCount dayCount, OptionType optionType,
                         ExcerciseType excerciseType, int version, String tickerSymbol, long insertTime) {
        super(id, name,  version, insertTime);
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


}
