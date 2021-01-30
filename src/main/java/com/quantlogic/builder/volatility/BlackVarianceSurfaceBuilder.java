package com.quantlogic.builder.volatility;

import com.quantlogic.builder.VolatilitySurfaceBuilder;
import com.quantlogic.enumtype.USMarketType;
import com.quantlogic.enumtype.DayCount;
import com.quantlogic.util.DateUtil;
import org.quantlib.*;

import java.util.Arrays;
import java.util.Calendar;
import java.util.stream.IntStream;

public class BlackVarianceSurfaceBuilder extends VolatilitySurfaceBuilder {

    private final Calendar calendar = Calendar.getInstance();
    private Date curValuationDate ;
    private UnitedStates market;
    private DateVector expirationDates;
    private DoubleVector strikeVals;
    private Matrix volMatrix ;
    private DayCounter dayCounter;

    public BlackVarianceSurfaceBuilder() {
    }

    public BlackVarianceSurfaceBuilder withValuationDate(long valuationDate) {
       this.curValuationDate = DateUtil.fromEpochMillis(valuationDate);
       return this;
    }

    private void zeroAll() {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    public BlackVarianceSurfaceBuilder withMarketCalendar(USMarketType calendar){
        this.market = new UnitedStates(calendar.getMarket());
        return this;
    }

    public BlackVarianceSurfaceBuilder withExpiration(long[] expirations){
        this.expirationDates = new DateVector(Arrays.stream(expirations).mapToObj(DateUtil::fromEpochMillis).toArray(Date[]::new));
        return this;
    }

    public BlackVarianceSurfaceBuilder withStrikes(double[] strikes){
        this.strikeVals = new DoubleVector(Arrays.stream(strikes).toArray());
        return this;
    }

    public BlackVarianceSurfaceBuilder withDayCounter(DayCount curDayCounter){
        this.dayCounter = curDayCounter.getDayCounter();
        return this;
    }

    public BlackVarianceSurfaceBuilder withVolMatrix(double[][] vols){
        volMatrix = new Matrix(vols.length, vols.length);
        IntStream.range(0, vols.length).forEach(i -> {
            IntStream.range(0, vols[i].length).forEach(j -> volMatrix.set(i, j, vols[i][j]));
        });
        return this;
    }

    public BlackVarianceSurface build(){
        return new BlackVarianceSurface(curValuationDate, market, expirationDates, strikeVals, volMatrix, dayCounter);
    }


}
