package com.quantlogic.rulegraph;

import com.quantlogic.entity.Entity;

import java.util.function.Function;
import java.util.function.Predicate;

public interface RuleStoreAttribute<T extends Entity, U extends  Entity> {

    boolean isInstrumentAttribute();
    Predicate<T> getInstrumentAttribute();
    Function<T, String> getEntityAttribute();

    RuleStoreAttribute DEFAULT = new RuleStoreAttribute() {
        @Override
        public boolean isInstrumentAttribute() {
            return false;
        }

        @Override
        public Predicate getInstrumentAttribute() {
            return null;
        }

        @Override
        public Function getEntityAttribute() {
            return null;
        }
    };

}
