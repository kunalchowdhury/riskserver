package com.quantlogic.enumtype;

import org.quantlib.Calendar;
import org.quantlib.TARGET;
import org.quantlib.UnitedStates;

import java.util.HashMap;
import java.util.Map;

public enum CalendarType {
    TARGET(new TARGET()),
    NYSE(new UnitedStates(UnitedStates.Market.NYSE));

    static private Map<String, CalendarType> map = new HashMap<>();

    static {
        for (CalendarType value : CalendarType.values()) {
            map.put(value.name() , value);
        }
    }

    private final Calendar calendar;
    CalendarType(Calendar calendar) {
        this.calendar = calendar;
    }

    public static CalendarType getCalendarType(String cal){
        return map.get(cal);
    }

    public Calendar getCalendar() {
        return calendar;
    }

}
