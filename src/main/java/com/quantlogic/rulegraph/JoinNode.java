package com.quantlogic.rulegraph;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.quantlogic.entity.Entity;
import com.quantlogic.entity.EntityRule;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;

import java.util.Optional;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JoinNode<T extends Entity, U extends Entity> implements Function<T, Pair<T, Integer>> {
    private final RootNode<T, U> rootNode ;
    private final TypeNode<T, U> typeNode ;

    @Value("${concurrency.level}")
    private int concurrency;

    private final Cache<Integer, EntityRule<T, U>> ruleCache;

    public JoinNode(RootNode<T, U> rootNode, TypeNode<T, U> typeNode) {
        this.rootNode = rootNode;
        this.typeNode = typeNode;
        this.ruleCache = CacheBuilder.newBuilder().concurrencyLevel(concurrency).build();
    }
    @Override
    public Pair<T, Integer> apply(T t) {
        EntityRule<T, U> rule = typeNode.getPredicatesAsMap()
                .entrySet()
                .stream()
                .filter(e -> e.getValue().getInstrumentAttribute().test(t))
                .map(e -> rootNode.getRule(e.getKey()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toCollection(TreeSet::new))
                .last();
        ruleCache.put(t.getId(), rule);
        return Pair.of(t, rule.getRuleIdentifier());
    }

    public Optional<EntityRule<T, U>> getApplicableRule(int id){
        return Optional.ofNullable(ruleCache.getIfPresent(id));
    }

}
