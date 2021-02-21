package com.quantlogic.engine;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.quantlogic.common.entity.SnapshotAllocationMessage;
import com.quantlogic.messaging.EngineRegistrationMessageProducer;
import com.quantlogic.valuation.entity.ErroredValuationResponse;
import com.quantlogic.valuation.entity.ValuationResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Component
public class ValuationOrchestrator {

    private final ValuationParameterRepository repository;
    private final PublishSubject<Pair<String, Integer>> spotUpdateSubject;
    private final PublishSubject<Pair<String, Integer>> volUpdateSubject;
    private final PublishSubject<Pair<String, Integer>> yieldCurveUpdateSubject;
    private final Map<String, ValuationExecutor> valuatorRegistry;
    private final Map<Pair<String, Integer>, Set<String>> spotValuatorMap;
    private final Map<Pair<String, Integer>, Set<String>> volValuatorMap;
    private final Map<Pair<String, Integer>, Set<String>> yieldValuatorMap;
    private final static Logger LOGGER = LoggerFactory.getLogger(ValuationOrchestrator.class);
    private final EngineRegistrationMessageProducer engineRegistrationProducer;

    public ValuationOrchestrator(@Autowired ValuationParameterRepository repository,
                                 @Autowired EngineRegistrationMessageProducer engineRegistrationMessageProducer) {
        this.repository = repository;
        this.spotUpdateSubject = PublishSubject.create();
        this.volUpdateSubject = PublishSubject.create();
        this.yieldCurveUpdateSubject = PublishSubject.create();
        this.valuatorRegistry = Maps.newHashMap();
        this.spotValuatorMap = Maps.newHashMap();
        this.volValuatorMap = Maps.newHashMap();
        this.yieldValuatorMap = Maps.newHashMap();
        this.engineRegistrationProducer = engineRegistrationMessageProducer;

        this.spotUpdateSubject
                .observeOn(Schedulers.from(Executors.newSingleThreadExecutor()))
                .doOnNext(triggerRecalcOnSpotUpdate())
                .subscribe();
        this.volUpdateSubject
                .observeOn(Schedulers.from(Executors.newSingleThreadExecutor()))
                .doOnNext(triggerRecalcOnVolUpdate())
                .subscribe();
        this.yieldCurveUpdateSubject
                .observeOn(Schedulers.from(Executors.newSingleThreadExecutor()))
                .doOnNext(triggerRecalcOnYieldCurveUpdate())
                .subscribe();
    }

    private Action1<? super Pair<String, Integer>> triggerRecalcOnYieldCurveUpdate() {
        return getAction1(yieldValuatorMap, ValuationParameterRepository.ParameterType.YIELDCURVE);
    }

    private Action1<? super Pair<String, Integer>> triggerRecalcOnVolUpdate() {
        return getAction1(volValuatorMap, ValuationParameterRepository.ParameterType.VOL);
    }

    private Action1<? super Pair<String, Integer>> triggerRecalcOnSpotUpdate() {
        return getAction1(spotValuatorMap, ValuationParameterRepository.ParameterType.SPOT);
    }

    private void sendResponse(ValuationResponse valuationResponse) {
        LOGGER.info("Sending valuation response {} " , valuationResponse);
    }

    private Action1<? super Pair<String, Integer>> getAction1(Map<Pair<String, Integer>, Set<String>> valuatorMap,
                                             ValuationParameterRepository.ParameterType parameterType) {
        return (Action1<Pair<String, Integer>>) add -> valuatorMap.get(add)
                .stream()
                .map(valuatorId -> CompletableFuture.supplyAsync(() -> {
                    ValuationExecutor valuationExecutor = valuatorRegistry.get(valuatorId);
                    switch (parameterType) {
                        case SPOT:
                            LOGGER.info("Triggering recalculation for spot updates for address {} ", add);
                            valuationExecutor.modifyValuatorSpot(add.getValue());
                            break;
                        case VOL:
                            LOGGER.info("Triggering recalculation for vol updates");
                            valuationExecutor.modifyValuatorVol(add.getValue());
                            break;
                        case YIELDCURVE:
                            LOGGER.info("Triggering recalculation for yield curve updates");
                            valuationExecutor.modifyValuatorYieldCurve(add.getValue());
                            break;
                    }
                    try {
                        return valuationExecutor.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return ErroredValuationResponse.INSTANCE;
                })).collect(Collectors.toList())
                .stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList()).forEach(this::sendResponse);
    }

    public void processSnapshotAllocationMessage(SnapshotAllocationMessage message) {
        if(message.isUpdate()){
            processUpdates(message);
        }else {
            LOGGER.info("Processing Snapshot Allocation Message {} ", message);
            int startMemAddress = 0;
            String[] spotIds = StringUtils.isNotEmpty(message.getSpotIds()) ? message.getSpotIds().split(",") : null;
            String[] volIds = StringUtils.isNotEmpty(message.getVolIds()) ? message.getVolIds().split(",") : null;
            String[] yieldCurveIds = StringUtils.isNotEmpty(message.getYieldCurveIds()) ? message.getYieldCurveIds().split(",") : null;
            repository.init(message.getMappedFile(), message.getSz(), message.getCacheId());
            int count = 0;
            if (spotIds != null) {
                for (String spotId : spotIds) {
                    int key = startMemAddress + count;
                    repository.setSpotAddress(message.getCacheId(), spotId, key);
                    repository.setParameterType(message.getCacheId(), key, ValuationParameterRepository.ParameterType.SPOT);
                    count += 4;
                    LOGGER.info("Address set for spotId {} at address {} ", spotId, key);
                }
            }
            if (volIds != null) {
                for (String volId : volIds) {
                    int key = startMemAddress + count;
                    repository.setVolAddress(message.getCacheId(), volId, key);
                    repository.setParameterType(message.getCacheId(), key, ValuationParameterRepository.ParameterType.VOL);
                    count += 4;
                    LOGGER.info("Address set for volId {} at address {} ", volId, key);
                }
            }

            if (yieldCurveIds != null) {
                for (String yieldCurveId : yieldCurveIds) {
                    int key = startMemAddress + count;
                    repository.setYieldCacheAddress(message.getCacheId(), yieldCurveId, key);
                    repository.setParameterType(message.getCacheId(), key, ValuationParameterRepository.ParameterType.YIELDCURVE);
                    count += 4;
                    LOGGER.info("Address set for yieldCurveId {} at address {} ", yieldCurveId, key);
                }
            }
            engineRegistrationProducer.receivedResponse(message.getCorrelationId());
            LOGGER.info("Initialization Complete.");
        }
    }

    public void processUpdates(SnapshotAllocationMessage message) {
        if (message.isUpdate()) {
            LOGGER.info("Update message received {} ", message);
            message.getAddressUpdates().forEach(add -> {
                ValuationParameterRepository.ParameterType parameterType = repository.getParameterType(message.getCacheId(), add);
                switch (parameterType) {
                    case SPOT:
                        LOGGER.info("Processing spot updates");
                        spotUpdateSubject.onNext(Pair.of(message.getCacheId(), add));
                        break;
                    case VOL:
                        LOGGER.info("Processing vol updates");
                        volUpdateSubject.onNext(Pair.of(message.getCacheId(), add));
                        break;
                    case YIELDCURVE:
                        LOGGER.info("Processing yield curve updates");
                        yieldCurveUpdateSubject.onNext(Pair.of(message.getCacheId(), add));
                        break;
                }
            });
        }

    }

    public void registerValuator(ValuationExecutor valuationExecutor) {
        LOGGER.info("Registering valuator with id {}", valuationExecutor.getId());
        this.valuatorRegistry.put(valuationExecutor.getId(), valuationExecutor);
        valuationExecutor.spotInterests().forEach(spotAddress ->
                this.spotValuatorMap.computeIfAbsent(Pair.of(valuationExecutor.getCurEngineId(), spotAddress),aLong -> Sets.newHashSet()).add(valuationExecutor.getId()));
        valuationExecutor.volInterests().forEach(volAddress ->
                this.volValuatorMap.computeIfAbsent(Pair.of(valuationExecutor.getCurEngineId(), volAddress), aLong -> Sets.newHashSet()).add(valuationExecutor.getId()));
        valuationExecutor.yieldCurveInterests().forEach(yieldCurveAddress ->
                this.yieldValuatorMap.computeIfAbsent(Pair.of(valuationExecutor.getCurEngineId(), yieldCurveAddress), aLong -> Sets.newHashSet()).add(valuationExecutor.getId()));
        LOGGER.info("Registration complete for valuator with id {}", valuationExecutor.getId());
    }
}
