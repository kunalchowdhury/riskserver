package com.quantlogic.rules;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.quantlogic.entity.EntityRule;
import com.quantlogic.entity.Instrument;
import com.quantlogic.entity.Volatility;
import com.quantlogic.ruleprocessor.RuleProcessorListener;
import org.springframework.beans.factory.annotation.Value;
import rx.subjects.PublishSubject;

import java.util.Collection;

public class DefaultInstrumentVolatilityRuleSet extends EntityRuleSet<Instrument, Volatility>{
    private final int ruleSetId ;
    private final Cache<Integer, EntityRule<Instrument, Volatility>> ruleCache;
    private final RuleProcessorListener<Instrument, Volatility> listener;
    private final PublishSubject<EntityRule<Instrument, Volatility>> subject;

    @Value("${concurrency.level}")
    private int concurrency;

    public DefaultInstrumentVolatilityRuleSet(int ruleSetId) {
        this.ruleSetId = ruleSetId;
        this.ruleCache = CacheBuilder.newBuilder().concurrencyLevel(concurrency).build();
        this.subject = PublishSubject.create();
        this.listener = new RuleProcessorListener<Instrument, Volatility>(){
            @Override
            public PublishSubject<EntityRule<Instrument, Volatility>> getSubject() {
                return subject;
            }

            @Override
            public void onRule(EntityRule<Instrument, Volatility> entityRule) {
                subject.onNext(entityRule);
            }
        };
    }

    @Override
    public int ruleSetId() {
        return ruleSetId;
    }

    @Override
    public void addEntityRule(EntityRule<Instrument, Volatility> entityRule) {
        listener.onRule(entityRule);
    }

    @Override
    public Collection<EntityRule<Instrument, Volatility>> get() {
        return ruleCache.asMap().values();
    }
}
