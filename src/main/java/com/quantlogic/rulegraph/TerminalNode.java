package com.quantlogic.rulegraph;

import com.quantlogic.entity.Entity;
import org.apache.commons.lang3.tuple.Pair;

import java.util.function.Function;

public class TerminalNode <T extends Entity, U extends Entity> implements Function<Pair<T, Integer>, String> {
    private TypeNode<T, U> typeNode;

    public TerminalNode(TypeNode<T, U> typeNode) {
        this.typeNode = typeNode;
    }


    @Override
    public String apply(Pair<T, Integer> tIntegerPair) {
        RuleStoreAttribute<T, U> ruleStoreAttribute =
                typeNode.getPredicate(tIntegerPair.getRight()).orElseGet(() -> RuleStoreAttribute.DEFAULT);
        return ruleStoreAttribute.getEntityAttribute().apply(tIntegerPair.getLeft());
    }
}
