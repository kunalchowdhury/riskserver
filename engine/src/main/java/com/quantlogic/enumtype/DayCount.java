package com.quantlogic.enumtype;

import org.quantlib.*;

public enum DayCount {
    ACTUAL_360(new Actual360()),
    ACTUAL_364(new Actual364()),
    ACTUAL_365_FIXED(new Actual365Fixed()),
    ACTUAL(new ActualActual()),
    BUSINESS_252(new Business252()),
    THIRTY_360(new Thirty360()),
    THIRTY_365(new Thirty365());

    DayCounter dayCounter;
    DayCount(DayCounter dayCounter) {
        this.dayCounter = dayCounter;
    }

    public DayCounter getDayCounter() {
        return dayCounter;
    }
}
