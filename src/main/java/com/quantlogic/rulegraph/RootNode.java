package com.quantlogic.rulegraph;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.quantlogic.entity.Entity;
import com.quantlogic.entity.EntityRule;
import com.quantlogic.ruleprocessor.RuleProcessorListener;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import rx.Observable;
import rx.subjects.PublishSubject;

import java.util.Optional;

public class RootNode<T extends Entity, U extends Entity> implements Processor {
    private final RuleProcessorListener<T, U> listener;
    private final Pair<TypeNode<T, U>, TypeNode<T, U>> typeNodeTypeNodePair;
    @Value("${concurrency.level}")
    private int concurrency;

    private final Cache<Integer, EntityRule<T, U>> ruleCache;

    public RootNode(RuleProcessorListener<T, U> listener) {
        this.listener = listener;
        this.typeNodeTypeNodePair = Pair.of(new TypeNode<>(TypeNode.TYPE_PARAM.Instrument, PublishSubject.create()),
                new TypeNode<>(TypeNode.TYPE_PARAM.Entity, PublishSubject.create()));
        this.ruleCache = CacheBuilder.newBuilder().concurrencyLevel(concurrency).build();
    }

    @Override
    public void exec() {
        PublishSubject<EntityRule<T, U>> incomingSubject = this.listener.getSubject();
        Observable<EntityRule<T, U>> source = incomingSubject.asObservable();
        source.subscribe(e -> this.ruleCache.put(e.getRuleIdentifier(), e));
        source.subscribe(typeNodeTypeNodePair.getLeft().getEntityRulePublishSubject());
        source.subscribe(typeNodeTypeNodePair.getRight().getEntityRulePublishSubject());
    }

    public Optional<EntityRule<T, U>> getRule(int k){
         return Optional.ofNullable(this.ruleCache.getIfPresent(k));
    }


}
