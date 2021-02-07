package com.quantlogic.ruleprocessor;

import com.quantlogic.entity.EntityRule;
import com.quantlogic.entity.Instrument;
import com.quantlogic.entity.Volatility;
import org.springframework.stereotype.Component;
import rx.subjects.PublishSubject;

@Component
public class DefaultRuleProcessorListener implements RuleProcessorListener<Instrument, Volatility>{
    private PublishSubject<EntityRule<Instrument, Volatility>> subject;

    public DefaultRuleProcessorListener() {
        this.subject = PublishSubject.create();
    }

    @Override
    public PublishSubject<EntityRule<Instrument, Volatility>> getSubject() {
        return subject;
    }
}
