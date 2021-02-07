package com.quantlogic.rulegraph;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.quantlogic.entity.Entity;
import com.quantlogic.entity.EntityRule;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JoinNode<T extends Entity, U extends Entity> implements Function<T, Pair<T, Integer>> {
    private final RootNode<T, U> rootNode ;
    private final TypeNode<T, U> typeNode ;

    private final Cache<Integer, EntityRule<T, U>> ruleCache;

    public JoinNode(RootNode<T, U> rootNode, TypeNode<T, U> typeNode, int concurrency) {
        this.rootNode = rootNode;
        this.typeNode = typeNode;
        this.ruleCache = CacheBuilder.newBuilder().concurrencyLevel(concurrency).build();
    }
    @Override
    public Pair<T, Integer> apply(T t) {
        List<EntityRule<T, U>> rule = typeNode.getPredicatesAsMap()
                .entrySet()
                .stream()
                .filter(e -> e.getValue().apply(t).test(t))
                .map(e -> rootNode.getRule(e.getKey()))
                .filter(Optional::isPresent)
                .map(Optional::get).sorted((o1, o2) -> o2.getRuleWeight() - o1.getRuleWeight()).collect(Collectors.toList());
        ruleCache.put(t.getId(), rule.get(0));
        return Pair.of(t, rule.get(0).getRuleIdentifier());
    }

    public Optional<EntityRule<T, U>> getApplicableRule(int id){
        return Optional.ofNullable(ruleCache.getIfPresent(id));
    }

}
