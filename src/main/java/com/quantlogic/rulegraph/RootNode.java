package com.quantlogic.rulegraph;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.quantlogic.entity.Entity;
import com.quantlogic.entity.EntityRule;
import com.quantlogic.riskserver.ConfigProperties;
import com.quantlogic.ruleprocessor.RuleProcessorListener;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import rx.Observable;
import rx.subjects.PublishSubject;

import java.util.Optional;

@Component
@ComponentScan(basePackages = "com.quantlogic")
public class RootNode<T extends Entity, U extends Entity> implements Processor {
    private final RuleProcessorListener<T, U> listener;
    private final Pair<TypeNode<T, U>, TypeNode<T, U>> typeNodeTypeNodePair;

    private final Cache<Integer, EntityRule<T, U>> ruleCache;
    private final int level;

    public RootNode(@Autowired RuleProcessorListener<T, U> listener, @Autowired ConfigProperties configProperties) {
        this.listener = listener;
        this.level = configProperties.getLevel();
        this.typeNodeTypeNodePair = Pair.of(new TypeNode<>(TypeNode.TypeParam.Instrument, PublishSubject.create(), level),
                new TypeNode<>(TypeNode.TypeParam.Entity, PublishSubject.create(), level));

        this.ruleCache = CacheBuilder.newBuilder().concurrencyLevel(level).build();

    }

    @Override
    public void exec() {
        PublishSubject<EntityRule<T, U>> incomingSubject = this.listener.getSubject();
        Observable<EntityRule<T, U>> source = incomingSubject.asObservable();
        source.subscribe(e ->  {this.ruleCache.put(e.getRuleIdentifier(), e); });
        source.subscribe(typeNodeTypeNodePair.getLeft().getEntityRulePublishSubject());
        source.subscribe(typeNodeTypeNodePair.getRight().getEntityRulePublishSubject());
    }

    public Optional<EntityRule<T, U>> getRule(int k){
         return Optional.ofNullable(this.ruleCache.getIfPresent(k));
    }


    public RuleProcessorListener<T, U> getListener() {
        return listener;
    }

    public Pair<TypeNode<T, U>, TypeNode<T, U>> getTypeNodeTypeNodePair() {
        return typeNodeTypeNodePair;
    }

    public Cache<Integer, EntityRule<T, U>> getRuleCache() {
        return ruleCache;
    }

    public int getLevel() {
        return level;
    }
}
