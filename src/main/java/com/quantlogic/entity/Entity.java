package com.quantlogic.entity;

public interface Entity {
    enum Type { INSTRUMENT, VOLATILITY_SURFACE, YIELD_CURVE }
    Type getType();
    int getId();
    String getName();
}
