package com.quantlogic.rulegraph;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.quantlogic.entity.Entity;
import com.quantlogic.entity.EntityRule;
import org.apache.commons.lang3.tuple.Pair;
import rx.subjects.PublishSubject;

import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.function.Predicate;

public class TypeNode<T extends Entity, U extends  Entity> implements Processor{
    private final TypeParam type;
    private final Cache<Integer, Function<T, Predicate<T>>> instrumentPredicateCache;
    private final PublishSubject<EntityRule<T, U>> entityRulePublishSubject;
    private final Cache<Integer, Function<Pair<T, U>, Function<T, String>>> parameterFunctionCache;


    public TypeNode(TypeParam type, PublishSubject<EntityRule<T, U>> entityRulePublishSubject, int concurrency) {
        this.type = type;
        this.entityRulePublishSubject = entityRulePublishSubject;
        this.instrumentPredicateCache = CacheBuilder.newBuilder().concurrencyLevel(concurrency).build();
        this.parameterFunctionCache = CacheBuilder.newBuilder().concurrencyLevel(concurrency).build();
    }

    @Override
    public void exec() {
        switch (type) {
            case Instrument:
                entityRulePublishSubject.map(e -> Pair.of(e.getRuleIdentifier(),
                        e))
                        .subscribe(e -> {
                            instrumentPredicateCache.put(e.getLeft(), t -> {
                                e.getRight().from(t, null);
                                return e.getRight().getPredicate();
                            });
                        });

                break;
            case Entity:
                entityRulePublishSubject.map(e -> Pair.of(e.getRuleIdentifier(),
                        e))
                        .subscribe(e -> {
                            parameterFunctionCache.put(e.getLeft(), tuPair -> {
                                e.getRight().from(tuPair.getLeft(), tuPair.getRight());
                                return e.getRight().getKeyTransform();
                            });
                        });

                break;
        }
    }

    public Optional<Function<Pair<T, U>, Function<T, String>>> getKeyTransform(int key){
        return Optional.ofNullable(this.parameterFunctionCache.getIfPresent(key));
    }

    public PublishSubject<EntityRule<T, U>> getEntityRulePublishSubject() {
        return entityRulePublishSubject;
    }

    public Optional<Function<T, Predicate<T>>> getPredicate(int key){
        return Optional.ofNullable(this.instrumentPredicateCache.getIfPresent(key));
    }

    public ConcurrentMap<Integer, Function<T, Predicate<T>>> getPredicatesAsMap(){
        return this.instrumentPredicateCache.asMap();
    }

    enum TypeParam {Instrument, Entity}


}
