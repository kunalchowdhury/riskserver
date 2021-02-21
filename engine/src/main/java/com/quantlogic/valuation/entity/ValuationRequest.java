package com.quantlogic.valuation.entity;

import java.util.Map;

public interface ValuationRequest {
    Map<String, Integer> getSpotPriceAddressMap();
    Map<String, Integer> getVolsAddressMap();
    Map<String, Integer> getYCAddressMap();

}
