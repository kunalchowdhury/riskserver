package com.quantlogic.ruleprocessor;

import com.quantlogic.entity.Entity;
import com.quantlogic.entity.EntityRule;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public interface EntityRuleParserSubscriber<T extends Entity, U extends Entity> {
    void init(RuleProcessorListener<T, U> listener);
    void subscribe(KafkaConsumer<String, EntityRule<T, U>> consumer, RuleProcessorListener<T, U> listener);
}
