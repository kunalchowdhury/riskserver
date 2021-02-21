package com.quantlogic.engine;

public interface InstrumentRuleRepository {
    String getPrimarySpotKey(String primaryInstrument, String secondaryInstrument);
    String getVolSurfaceKey(String primaryInstrument, String secondaryInstrument);
    String getYieldCurveKey(String primaryInstrument, String secondaryInstrument);
}
