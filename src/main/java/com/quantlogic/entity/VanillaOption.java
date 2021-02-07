package com.quantlogic.entity;

import com.quantlogic.annotation.BaseEntity;
import com.quantlogic.enumtype.DayCount;
import com.quantlogic.enumtype.ExcerciseType;
import com.quantlogic.enumtype.OptionType;

@BaseEntity
public class VanillaOption extends Instrument{

    private Double strike ;
    private String underlying ;
    private Double riskFreeRate ;
    private Double dividendYield;
    private Double volatility ;
    private Long settlementDate;
    private Long maturity;
    private DayCount dayCount;
    private OptionType optionType;
    private ExcerciseType excerciseType;
    private String tickerSymbol;



    public VanillaOption(int id, String name, int version, Long insertTime) {
        super(id, name, version, insertTime);
    }

    public VanillaOption(int id, String name, Double strike, String underlying,
                         Double riskFreeRate, Double dividendYield, Double volatility,
                         Long settlementDate, Long maturity, DayCount dayCount,
                         OptionType optionType, ExcerciseType excerciseType, int version,
                         String tickerSymbol, Long insertTime) {
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

    public Double getStrike() {
        return strike;
    }

    public String getUnderlying() {
        return underlying;
    }

    public Double getRiskFreeRate() {
        return riskFreeRate;
    }

    public Double getDividendYield() {
        return dividendYield;
    }

    public Double getVolatility() {
        return volatility;
    }

    public Long getSettlementDate() {
        return settlementDate;
    }

    public Long getMaturity() {
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
