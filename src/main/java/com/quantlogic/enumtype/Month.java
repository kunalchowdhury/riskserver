package com.quantlogic.enumtype;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static java.time.Month.*;
import static org.quantlib.Month.*;

public enum Month {
    JAN_MONTH(JANUARY, January),
    FEB_MONTH(FEBRUARY, February),
    MAR_MONTH(MARCH, March),
    APR_MONTH(APRIL, April),
    MAY_MONTH(MAY, May),
    JUN_MONTH(JUNE, June),
    JUL_MONTH(JULY, July),
    AUG_MONTH(AUGUST, August),
    SEP_MONTH(SEPTEMBER, September),
    OCT_MONTH(OCTOBER, October),
    NOV_MONTH(NOVEMBER, November),
    DEC_MONTH(DECEMBER, December)
    ;

    java.time.Month month;
    org.quantlib.Month qMonth;

    static Map<java.time.Month, org.quantlib.Month> javaToQuantLib = new EnumMap<>(java.time.Month.class);
    static Map<org.quantlib.Month, java.time.Month> quantLibToJavaMap = new HashMap<>();

    static {
        for (Month value : Month.values()) {
            javaToQuantLib.put(value.month, value.qMonth);
            quantLibToJavaMap.put(value.qMonth, value.month);
        }
    }

    public static org.quantlib.Month getQuantLibMonth(java.time.Month month){
        return javaToQuantLib.get(month);
    }

    public static java.time.Month getJavaMonth(org.quantlib.Month qMonth){
        return quantLibToJavaMap.get(qMonth);
    }


    Month(java.time.Month month, org.quantlib.Month qMonth) {
        this.month = month;
        this.qMonth = qMonth;

    }
}
