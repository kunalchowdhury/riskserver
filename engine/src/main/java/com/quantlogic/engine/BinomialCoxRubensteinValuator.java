package com.quantlogic.engine;

import com.google.common.collect.Maps;
import com.quantlogic.builder.BlackScholesMertonProcessBuilder;
import com.quantlogic.builder.BlackVolTermStructureBuilder;
import com.quantlogic.builder.volatility.BlackVarianceSurfaceBuilder;
import com.quantlogic.builder.yieldtermcurve.FlatForwardBuilder;
import com.quantlogic.builder.yieldtermcurve.YieldTermStructureBuilder;
import com.quantlogic.common.entity.*;
import com.quantlogic.enumtype.DayCount;
import com.quantlogic.enumtype.Month;
import com.quantlogic.enumtype.OptionType;
import com.quantlogic.enumtype.USMarketType;
import com.quantlogic.messaging.EngineRegistrationMessageProducer;
import com.quantlogic.valuation.entity.ValuationResponse;
import org.quantlib.Date;
import org.quantlib.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.function.Function;

@Component
public class BinomialCoxRubensteinValuator extends ValuationExecutor {

    private final ValuationParameterRepository repository;
    private final Map<Integer, Function<BlackScholesMertonProcessBuilder, Void>> spotTransformMap ;
    private final Map<Integer, Function<BlackScholesMertonProcessBuilder, Void>> volTransformMap ;
    private final InstrumentRuleRepository ruleRepo;
    private final EngineConfig engineConfig;
    private final EngineRegistrationMessage engineRegistrationMessage;
    private final String curEngineId;
    private TimedVanillaOption instrument;
    private final BlackScholesMertonProcessBuilder blackScholesMertonProcessBuilder;
    private VanillaOption americanOption;
    private final ValuationResponse valuationResponse;
    private final String valuatorId ;
    private static final Logger LOGGER = LoggerFactory.getLogger(BinomialCoxRubensteinValuator.class);
    private final EngineRegistrationMessageProducer engineRegistrationMessageProducer;

    public BinomialCoxRubensteinValuator(@Autowired ValuationParameterRepository repository,
                                         @Autowired DefaultInstrumentRuleRepoImpl defaultInstrumentRepo,
                                         @Autowired EngineConfig engineConfig,
                                         @Autowired EngineRegistrationMessageProducer engineRegistrationMessageProducer) {
        this.repository = repository;
        this.engineRegistrationMessageProducer = engineRegistrationMessageProducer;
        this.spotTransformMap = Maps.newConcurrentMap();
        this.volTransformMap = Maps.newConcurrentMap();
        this.ruleRepo = defaultInstrumentRepo;
        this.engineConfig = engineConfig;
        this.valuationResponse = new ValuationResponse();
        this.blackScholesMertonProcessBuilder = new BlackScholesMertonProcessBuilder();
        this.valuatorId = UUID.randomUUID().toString();
        this.curEngineId = ManagementFactory.getRuntimeMXBean().getName().replace("@", "_");
        this.engineRegistrationMessage = new EngineRegistrationMessage();
        engineRegistrationMessage.setHostId(curEngineId.split("_")[1]);
        engineRegistrationMessage.setPid(curEngineId.split("_")[0]);

    }

    @Override
    public void modifyValuatorSpot(int idx) {
        spotTransformMap.get(idx).apply(blackScholesMertonProcessBuilder);
        engineRegistrationMessageProducer.sendFreeAddressMarker(curEngineId, engineConfig.getMarkerTopic(), idx);
    }

    @Override
    public void modifyValuatorVol(int idx) {
        volTransformMap.get(idx).apply(blackScholesMertonProcessBuilder);
    }

    @Override
    public void modifyValuatorYieldCurve(int address) {
        throw new UnsupportedOperationException("not supported for this model");
    }

    @Override
    public void setInstrument(NamedTimedEntity namedTimedEntity) {
        this.instrument = (TimedVanillaOption) namedTimedEntity ;
        createValuationRequest();
    }

    @Override
    Set<Integer> spotInterests() {
        return spotTransformMap.keySet();
    }

    @Override
    Set<Integer> volInterests() {
        return volTransformMap.keySet();
    }

    @Override
    Set<Integer> yieldCurveInterests() {
        return Collections.emptySet();
    }

    @Override
    String getId() {
        return valuatorId;
    }

    private void createValuationRequest() {
        Settings.instance().setEvaluationDate(DateUtil.fromEpochMillis(instrument.getSnapshotTime()));
        LOGGER.info("Setting up spot");
        String primarySpotKeyFromRule = ruleRepo.getPrimarySpotKey(instrument.getName(), null);

        LOGGER.info("Setting up vol");
        String volKeyFromRule = ruleRepo.getVolSurfaceKey(instrument.getName(), null);
        this.engineRegistrationMessage.setEngineSequence(engineConfig.getEngineId());
        this.engineRegistrationMessage.setSpotids(primarySpotKeyFromRule.split(",")[0]);
        this.engineRegistrationMessage.setVolIds(volKeyFromRule.split(",")[0]);

        LOGGER.info("Sending registration message {} ", engineRegistrationMessage);
        engineRegistrationMessageProducer.sendEngineRegistrationMessage(engineRegistrationMessage);
        setSpotParameters(primarySpotKeyFromRule);
        setVolParameters(volKeyFromRule);
        Date settlementDate = DateUtil.fromEpochMillis(instrument.getSettlementDate());
        prepareBlackScholesMertonProcess(settlementDate);
        PlainVanillaPayoff payoff = new PlainVanillaPayoff(OptionType.values()[instrument.getOptionType()].type,
                instrument.getStrike());
        Exercise americanExercise = new AmericanExercise(settlementDate,
                DateUtil.fromEpochMillis(instrument.getMaturity()));
        this.americanOption =
                new VanillaOption(payoff, americanExercise);
    }

    private void prepareBlackScholesMertonProcess(Date settlementDate) {
        double riskFreeRate = instrument.getRiskFreeRate();
        DayCount dayCount = DayCount.values()[instrument.getDayCount()];
        FlatForwardBuilder builder = new FlatForwardBuilder();
        builder.withDayCount(dayCount);
        builder.withRiskFreeRate(riskFreeRate);
        builder.withSettlementDay(settlementDate.dayOfMonth());
        builder.withSettlementMonth(Month.getJavaMonth(settlementDate.month()));
        builder.withSettlementYear(settlementDate.year());
        YieldTermStructureBuilder yieldTermStructureBuilder = new YieldTermStructureBuilder();
        yieldTermStructureBuilder.withFlatForwardBuilder(builder);
        blackScholesMertonProcessBuilder.withYieldCurveBuilder(yieldTermStructureBuilder).withDividendBuilder(yieldTermStructureBuilder);
    }

    private void setSpotParameters(String primarySpotKeyFromRule) {
        LOGGER.info("Primary Spot Key {}", primarySpotKeyFromRule);
        String primarySpotKey = primarySpotKeyFromRule.split(",")[0];

        String version = primarySpotKeyFromRule.split(",")[1];
        Optional<SpotPrice> spotPrice = repository.getSpot(curEngineId, primarySpotKey, version);
        Optional<Integer> spotAddress = repository.getSpotAddress(curEngineId, primarySpotKey);
        if(spotPrice.isPresent() && spotAddress.isPresent()){
            LOGGER.info("Spot Instance {} and address {}", spotPrice.get(), spotAddress.get());
            blackScholesMertonProcessBuilder.withUnderlyingPrice(spotPrice.get().getMid());
            spotTransformMap.put(spotAddress.get(), blackScholesMertonProcessBuilder -> {
                Optional<SpotPrice> spot = repository.getSpot(curEngineId, primarySpotKey, null);
                spot.ifPresent(price -> blackScholesMertonProcessBuilder.withUnderlyingPrice(price.getMid()));
                return null;
            });
        }
    }

    private void setVolParameters(String volKeyFromRule) {
        LOGGER.info("Primary Vol Key {}", volKeyFromRule);
        String volKey = volKeyFromRule.split(",")[0];

        String version = volKeyFromRule.split(",")[1];
        Optional<TimedBlackVarianceVolatility> volatility = repository.getVol(curEngineId, volKey, version);
        Optional<Integer> volAddress = repository.getVolAddress(curEngineId, volKey);
        if(volatility.isPresent() && volAddress.isPresent()){
            LOGGER.info("Volatility Instance {} and address {}", volatility.get(), volAddress.get());
            BlackVarianceSurfaceBuilder blackVarianceSurfaceBuilder = new BlackVarianceSurfaceBuilder();
            setBlackVarianceSurfaceBuilder(blackVarianceSurfaceBuilder, volatility.get());
            blackScholesMertonProcessBuilder.withVolatilitySurfaceBuilder(new BlackVolTermStructureBuilder()
                    .withVolSurfaceBuilder(blackVarianceSurfaceBuilder));
            volTransformMap.put(volAddress.get(), blackScholesMertonProcessBuilder -> {
                Optional<TimedBlackVarianceVolatility> vol = repository.getVol(curEngineId, volKey, null);
                vol.ifPresent(volatility1 -> setBlackVarianceSurfaceBuilder(blackVarianceSurfaceBuilder, volatility1));
                return null;
            });
        }
    }

    private void setBlackVarianceSurfaceBuilder(BlackVarianceSurfaceBuilder blackVarianceSurfaceBuilder,
                                                TimedBlackVarianceVolatility timedBlackVarianceVolatility) {
        blackVarianceSurfaceBuilder.withDayCounter(DayCount.values()[timedBlackVarianceVolatility.getCurDayCounter()]);
        blackVarianceSurfaceBuilder.withExpiration(timedBlackVarianceVolatility.getExpirations());
        blackVarianceSurfaceBuilder.withStrikes(timedBlackVarianceVolatility.getStrikes());
        blackVarianceSurfaceBuilder.withVolMatrix(timedBlackVarianceVolatility.getVols());
        blackVarianceSurfaceBuilder.withMarketCalendar(USMarketType.values()[timedBlackVarianceVolatility.getCalendar()]);
        blackVarianceSurfaceBuilder.withValuationDate(timedBlackVarianceVolatility.getValuationDate());
    }

    @Override
    public ValuationResponse call() throws Exception {
        BinomialCRRVanillaEngineBuilder builder = new BinomialCRRVanillaEngineBuilder();
        builder.withProcess(blackScholesMertonProcessBuilder.build()).withSteps(engineConfig.getCrrSteps());
        americanOption.setPricingEngine(builder.build());
        valuationResponse.setNpv(americanOption.NPV());
        valuationResponse.setDelta(americanOption.delta());
        valuationResponse.setGamma(americanOption.gamma());
        LOGGER.info("Initial Valuation Result : {}", valuationResponse);
        return valuationResponse;
    }

    public String getCurEngineId() {
        return curEngineId;
    }
}
