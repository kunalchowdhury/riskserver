package com.quantlogic.rulegraph;

import com.quantlogic.entity.Entity;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Optional;
import java.util.function.Function;

public class TerminalNode <T extends Entity, U extends Entity> implements Function<Pair<Pair<T, U>, Integer>, String> {
    private final TypeNode<T, U> typeNode;

    public TerminalNode(TypeNode<T, U> typeNode) {
        this.typeNode = typeNode;
    }

    @Override
    public String apply(Pair<Pair<T, U>, Integer> pair) {
        Optional<Function<Pair<T, U>, Function<T, String>>> keyTransform = typeNode.getKeyTransform(pair.getRight());
        return keyTransform.map(func -> func.apply(pair.getLeft())
                .apply(pair.getLeft().getLeft())).orElse(StringUtils.EMPTY);
    }
}
