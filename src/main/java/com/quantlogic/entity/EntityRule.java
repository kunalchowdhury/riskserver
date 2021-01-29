package com.quantlogic.entity;

import java.util.function.Function;
import java.util.function.Predicate;

public interface EntityRule<T extends Entity, U extends Entity> extends Comparable<EntityRule<T, U>> {
    int getRuleIdentifier();
    int getRuleWeight();
    Predicate<T> getPredicate();
    Function<T, String> getKeyTransform();
    Class<U> getTargetClass();
    @Override
    default int compareTo(EntityRule<T, U> o){
        return this.getRuleWeight() - o.getRuleWeight();
    }
}
