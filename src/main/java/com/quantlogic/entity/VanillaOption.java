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
    private String pricingMethod;

    public VanillaOption(int id, String name) {
        super(id, name);
    }

}
