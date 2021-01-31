package com.quantlogic.rules;

import com.quantlogic.entity.Entity;
import com.quantlogic.entity.EntityRule;

import java.util.Collection;
import java.util.function.Supplier;

public abstract class EntityRuleSet<T extends Entity, U extends Entity> implements Supplier<Collection<EntityRule<T, U>>> {
    public abstract int ruleSetId();
    public abstract void addEntityRule(EntityRule<T, U> entityRule) ;

}
