package com.quantlogic.entity;

import org.quantlib.*;
import org.quantlib.VanillaOption;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoField;

public class EntityTest {
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

        Date todaysDate = new Date(15, Month.May, 1998);
        Date settlementDate = new Date(17, Month.May, 1998);
        Settings.instance().setEvaluationDate(todaysDate);

        Date maturity = new Date(17, Month.May, 1999);
        DayCounter dayCounter = new Actual365Fixed();
        Calendar calendar = new UnitedStates(UnitedStates.Market.NYSE);


        DateVector expirations = new DateVector(new Date[]{new Date(20, Month.December, 2013),
                new Date(17, Month.January, 2014), new Date(21, Month.March, 2014),
                new Date(20, Month.June, 2014), new Date(19, Month.September, 2014)});

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
