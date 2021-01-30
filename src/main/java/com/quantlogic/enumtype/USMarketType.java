package com.quantlogic.enumtype;

import org.quantlib.UnitedStates;

public enum USMarketType {
    NYSE(UnitedStates.Market.NYSE),
    FederalReserve(UnitedStates.Market.FederalReserve),
    GovernmentBond(UnitedStates.Market.GovernmentBond);

    UnitedStates.Market market;

    USMarketType(UnitedStates.Market market) {
        this.market = market;
    }

    public UnitedStates.Market getMarket() {
        return market;
    }
}
