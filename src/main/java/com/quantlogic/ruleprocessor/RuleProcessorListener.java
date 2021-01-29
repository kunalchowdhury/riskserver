package com.quantlogic.ruleprocessor;

import com.quantlogic.entity.Entity;
import com.quantlogic.entity.EntityRule;
import rx.subjects.PublishSubject;

public interface RuleProcessorListener<T extends Entity, U extends Entity> {
     PublishSubject<EntityRule<T, U>> getSubject();
     default void onRule(EntityRule<T, U> entityRule){
          getSubject().onNext(entityRule);
     }

}
