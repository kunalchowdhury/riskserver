package com.quantlogic.entity;

import com.quantlogic.entity.metadata.BlackVarianceVolSurfaceDelta;
import com.quantlogic.enumtype.DayCount;
import com.quantlogic.enumtype.USMarketType;
import com.quantlogic.util.ReflectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.quantlib.VanillaOption;
import org.quantlib.*;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.ZoneId;

public class EntityTest {

    @Test
    public void testDeltaEntity(){
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        zeroAll(calendar);
        calendar.set(15, java.util.Calendar.MAY, 15);
        long valuationDate = calendar.getTime().getTime();

        Long[] expirations = new Long[5];
        set(calendar, 2013, java.util.Calendar.DECEMBER, 20, expirations, 0);
        set(calendar, 2014, java.util.Calendar.JANUARY, 17, expirations, 1);
        set(calendar, 2014, java.util.Calendar.MARCH, 21, expirations, 2);
        set(calendar, 2014, java.util.Calendar.JUNE, 20, expirations, 3);
        set(calendar, 2014, java.util.Calendar.SEPTEMBER, 19, expirations, 4);

        Double[][] vols = new Double[][]{
                {0.15640, 0.15433, 0.16079 , 0.16394, 0.17383},
                {0.15343, 0.15240, 0.15804 , 0.16255, 0.17303},
                {0.15128, 0.14888, 0.15512 , 0.15944, 0.17038},
                {0.14798, 0.14906, 0.15522 , 0.16171, 0.16156},
                {0.14580, 0.14576, 0.15364 , 0.16037, 0.16042}
        };
        BlackVarianceVolatilitySurface vol = new BlackVarianceVolatilitySurface(100,
                "QQQ_Black_Variance", 1,
                valuationDate,
                valuationDate,
                USMarketType.NYSE,
                expirations,
                new Double[]{1650.0, 1660.0, 1670.0, 1675.0, 1680.0},
                DayCount.ACTUAL_365_FIXED,
                vols);

        BlackVarianceVolSurfaceDelta delta = new BlackVarianceVolSurfaceDelta("strikes",
                "1660.3", new int[]{2}, null,false);

        Assert.assertEquals(1670.0, vol.getStrikes()[2], 0);
        try {
            BlackVarianceVolatilitySurface modifiedEntity = ReflectionUtils.INSTANCE.getModifiedEntity(vol, delta);
            Assert.assertEquals(1660.3, vol.getStrikes()[2], 0);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void set(java.util.Calendar calendar, int year, int month, int day, Long[] expirations, int idx){
        zeroAll(calendar);
        calendar.set(year, month, day);
        expirations[idx] = calendar.getTime().getTime();
    }

    private void zeroAll(java.util.Calendar calendar) {
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calendar.set(java.util.Calendar.MINUTE, 0);
        calendar.set(java.util.Calendar.SECOND, 0);
        calendar.set(java.util.Calendar.MILLISECOND, 0);
    }

    public static void main(String[] args) {
        Matrix volMatrix = new Matrix(5 ,5);
        volMatrix.set(0,0 , 0.15640);
        volMatrix.set(0, 1, 0.15433);
        volMatrix.set(0, 2, 0.16079);
        volMatrix.set(0, 3, 0.16394);
        volMatrix.set(0, 4, 0.17383);

        volMatrix.set(1,0 , 0.15343);
        volMatrix.set(1, 1, 0.15240);
        volMatrix.set(1, 2, 0.15804);
        volMatrix.set(1, 3, 0.16255);
        volMatrix.set(1, 4, 0.17303);

        volMatrix.set(2,0 , 0.15128);
        volMatrix.set(2, 1, 0.14888);
        volMatrix.set(2, 2, 0.15512);
        volMatrix.set(2, 3, 0.15944);
        volMatrix.set(2, 4, 0.17038);

        volMatrix.set(3,0 , 0.14798);
        volMatrix.set(3, 1, 0.14906);
        volMatrix.set(3, 2, 0.15522);
        volMatrix.set(3, 3, 0.16171);
        volMatrix.set(3, 4, 0.16156);

        volMatrix.set(4,0 , 0.14580);
        volMatrix.set(4, 1, 0.14576);
        volMatrix.set(4, 2, 0.15364);
        volMatrix.set(4, 3, 0.16037);
        volMatrix.set(4, 4, 0.16042);

        Date todaysDate = new Date(22, Month.February, 2021);
        Date settlementDate = new Date(24, Month.February, 2021);
        Settings.instance().setEvaluationDate(todaysDate);

        Date maturity = new Date(24, Month.February, 2022);
        DayCounter dayCounter = new Actual365Fixed();
        Calendar calendar = new UnitedStates(UnitedStates.Market.NYSE);



        DateVector expirations = new DateVector(new Date[]{new Date(20, Month.December, 2026),
                new Date(17, Month.January, 2027), new Date(21, Month.March, 2027),
                new Date(20, Month.June, 2027), new Date(19, Month.September, 2027)});

        DoubleVector strikes = new DoubleVector(new double[]{1650.0, 1660.0, 1670.0, 1675.0, 1680.0});

        BlackVarianceSurface blackVarianceSurface = new BlackVarianceSurface(todaysDate, calendar, expirations, strikes, volMatrix, dayCounter);
        double blackVol = blackVarianceSurface.blackVol(expirations.get(0), 1650.0, true);
        System.out.println(blackVol);


        // our option
        Option.Type type = Option.Type.Put;
        double strike = 1656.0;
        double underlying = 1652.0;
        double riskFreeRate = 0.06;
        double dividendYield = 0.00;
        double volatility = 0.2;


        // define European, Bermudan, and American exercises
        DateVector exerciseDates = new DateVector();
        for (int i = 1; i <= 4; i++) {
            Date forward = settlementDate.add(new Period(3*i, TimeUnit.Months));
            exerciseDates.add(forward);
        }
        Exercise americanExercise = new AmericanExercise(settlementDate,
                maturity);

        QuoteHandle underlyingH = new QuoteHandle(new SimpleQuote(underlying));
        YieldTermStructureHandle flatTermStructure =
                new YieldTermStructureHandle(new FlatForward(
                        settlementDate, riskFreeRate, dayCounter));
        YieldTermStructureHandle flatDividendYield =
                new YieldTermStructureHandle(new FlatForward(
                        settlementDate, dividendYield, dayCounter));
        BlackVolTermStructureHandle blackVarianceSurfaceHandle =
                new BlackVolTermStructureHandle(blackVarianceSurface);

        BlackScholesMertonProcess stochasticProcess =
                new BlackScholesMertonProcess(underlyingH,
                        flatDividendYield,
                        flatTermStructure,
                        blackVarianceSurfaceHandle);

        // options
        PlainVanillaPayoff payoff = new PlainVanillaPayoff(type, strike);


        org.quantlib.VanillaOption americanOption =
                new VanillaOption(payoff, americanExercise);


        String fmt = "NPV of instrument using %34s = %13.9f\n";

        String method = "Binomial Leisen-Reimer";

        int timeSteps = 801;

        americanOption.setPricingEngine(
                new BinomialLRVanillaEngine(stochasticProcess, timeSteps));
        System.out.printf(fmt, method, americanOption.NPV());

        method = "Binomial Cox-Ross-Rubinstein";

        americanOption.setPricingEngine(
                new BinomialCRRVanillaEngine(stochasticProcess, timeSteps));
        System.out.printf(fmt, method, americanOption.NPV());

        method = "Binomial Joshi";

        americanOption.setPricingEngine(
                new BinomialJ4VanillaEngine(stochasticProcess, timeSteps));
        System.out.printf(fmt, method, americanOption.NPV());


        java.util.Calendar calendar1 = java.util.Calendar.getInstance();
        calendar1.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calendar1.set(java.util.Calendar.MINUTE, 0);
        calendar1.set(java.util.Calendar.SECOND, 0);
        calendar1.set(java.util.Calendar.MILLISECOND, 0);

        calendar1.setTimeInMillis(System.currentTimeMillis());
        LocalDate localDate = calendar1.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        System.out.println(localDate.getMonth());
        System.out.println(localDate.getDayOfMonth());
        System.out.println(localDate.getYear());


        //BlackVarianceSurface blackVarianceSurface = new BlackVarianceSurface()
    }
}
