package com.quantlogic.entity;

import java.util.function.Function;
import java.util.function.Predicate;

public interface EntityRule<T extends Entity, U extends Entity>  {
    int getRuleIdentifier();
    int getRuleWeight();
    Predicate<T> getPredicate();
    Function<T, String> getKeyTransform();
    T getSource();
    U getDestination();
    void from(T t, U u);

}
