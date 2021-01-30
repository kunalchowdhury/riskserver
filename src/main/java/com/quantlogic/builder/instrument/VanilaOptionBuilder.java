package com.quantlogic.builder.instrument;

import com.quantlogic.builder.InstrumentBuilder;
import com.quantlogic.util.DateUtil;
import com.quantlogic.util.TypeConversionUtil;
import org.quantlib.AmericanExercise;
import org.quantlib.Exercise;
import org.quantlib.PlainVanillaPayoff;
import org.quantlib.VanillaOption;

public class VanilaOptionBuilder extends InstrumentBuilder {
    private com.quantlogic.entity.VanillaOption option;

    public VanilaOptionBuilder() {
    }
    public VanilaOptionBuilder withVanillaOption(com.quantlogic.entity.VanillaOption vanillaOption){
        this.option = vanillaOption;
        return this;
    }
    @Override
    public VanillaOption build() {
        PlainVanillaPayoff payoff = new PlainVanillaPayoff(TypeConversionUtil.fromEntityOptionType(option.getOptionType()), option.getStrike());
        Exercise exercise;
        switch (option.getExcerciseType()){
            case AMERICAN:
                exercise = new AmericanExercise(DateUtil.fromEpochMillis(option.getSettlementDate()),
                        DateUtil.fromEpochMillis(option.getMaturity()));
                return new VanillaOption(payoff, exercise);
            case EUROPEAN:
                break;
            case BERMUDEAN:
                break;
        }
        return null;
    }
}
