package com.quantlogic.engine;

import com.quantlogic.common.entity.NamedTimedEntity;
import com.quantlogic.valuation.entity.ValuationResponse;

import java.util.Set;
import java.util.concurrent.Callable;

public abstract class ValuationExecutor implements Callable<ValuationResponse> {
    abstract void modifyValuatorSpot(int idx);
    abstract void modifyValuatorVol(int idx);
    abstract void modifyValuatorYieldCurve(int idx);
    abstract void setInstrument(NamedTimedEntity namedTimedEntity);
    abstract Set<Integer> spotInterests();
    abstract Set<Integer> volInterests();
    abstract Set<Integer> yieldCurveInterests();
    abstract String getId();
    abstract String getCurEngineId();
}
