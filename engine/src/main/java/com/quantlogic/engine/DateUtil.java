package com.quantlogic.engine;

import com.quantlogic.enumtype.Month;
import org.quantlib.Date;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;

public final class DateUtil {
    private static final Calendar calendar = Calendar.getInstance();

    public static Date fromEpochMillis(long epoch){
        zeroAll();
        calendar.setTimeInMillis(epoch);
        LocalDate localDate = calendar.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return new Date(localDate.getDayOfMonth(), Month.getQuantLibMonth(localDate.getMonth()), localDate.getYear());
    }

    private static void zeroAll() {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }


}
