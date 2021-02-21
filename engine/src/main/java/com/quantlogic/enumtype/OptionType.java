package com.quantlogic.enumtype;

import org.quantlib.Option;

public enum OptionType {
    CALL(Option.Type.Call), PUT(Option.Type.Put);

    public final Option.Type type;
    OptionType(Option.Type type) {
        this.type = type;
    }
}
