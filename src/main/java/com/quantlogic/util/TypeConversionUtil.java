package com.quantlogic.util;

import com.quantlogic.enumtype.OptionType;
import org.quantlib.Option;

public final class TypeConversionUtil {
    public static Option.Type fromEntityOptionType(OptionType optionType){
        switch (optionType){
            case PUT:
                return Option.Type.Put;
            case CALL:
                return Option.Type.Call;
        }
        throw new IllegalStateException("Unknown type "+optionType);
    }

}
