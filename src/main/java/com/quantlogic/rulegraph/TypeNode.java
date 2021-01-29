package com.quantlogic.rulegraph;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.quantlogic.entity.Entity;
import com.quantlogic.entity.EntityRule;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import rx.subjects.PublishSubject;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class TypeNode<T extends Entity, U extends  Entity> implements Processor{
    enum TYPE_PARAM {Instrument, Entity}

    private final TYPE_PARAM type;
    private final PublishSubject<EntityRule<T, U>> entityRulePublishSubject;

    @Value("${concurrency.level}")
    private int concurrency;

    private final Cache<Integer, RuleStoreAttribute<T, U>> instrumentPredicateCache;

    private final Cache<Integer, RuleStoreAttribute<T, U>> parameterFunctionCache;

    public TypeNode(TYPE_PARAM type, PublishSubject<EntityRule<T, U>> entityRulePublishSubject) {
        this.type = type;
        this.entityRulePublishSubject = entityRulePublishSubject;
        this.instrumentPredicateCache = CacheBuilder.newBuilder().concurrencyLevel(concurrency).build();
        this.parameterFunctionCache = CacheBuilder.newBuilder().concurrencyLevel(concurrency).build();
    }

    @Override
    public void exec() {
        switch (type){
            case Instrument:
                entityRulePublishSubject.map(e -> Pair.of(e.getRuleIdentifier(),
                                e.getPredicate()))
                        .subscribe(e -> instrumentPredicateCache.put(e.getLeft(), new RuleStoreAttribute<T, U>() {
                            @Override
                            public boolean isInstrumentAttribute() {
                                return true;
                            }

                            @Override
                            public Predicate<T> getInstrumentAttribute() {
                                return e.getRight();
                            }

                            @Override
                            public Function<T, String> getEntityAttribute() {
                                throw new UnsupportedOperationException();
                            }
                        }));

                break;
            case Entity:
                entityRulePublishSubject.map(e -> Pair.of(e.getRuleIdentifier(),
                         e.getKeyTransform()))
                        .subscribe(e -> parameterFunctionCache.put(e.getLeft(), new RuleStoreAttribute<T, U>() {
                            @Override
                            public boolean isInstrumentAttribute() {
                                return false;
                            }

                            @Override
                            public Predicate<T> getInstrumentAttribute() {
                                throw new UnsupportedOperationException();
                            }

                            @Override
                            public Function<T, String> getEntityAttribute() {
                                return e.getRight();
                            }
                        }));

                break;

        }

    }

    public PublishSubject<EntityRule<T, U>> getEntityRulePublishSubject() {
        return entityRulePublishSubject;
    }

    public Optional<RuleStoreAttribute<T, U>> getKeyTransform(int key){
        return Optional.ofNullable(this.parameterFunctionCache.getIfPresent(key));
    }

    public Optional<RuleStoreAttribute<T, U>> getPredicate(int key){
        return Optional.ofNullable(this.instrumentPredicateCache.getIfPresent(key));
    }

    public Map<Integer, RuleStoreAttribute<T, U>> getPredicatesAsMap(){
        return this.instrumentPredicateCache.asMap();
    }


}
