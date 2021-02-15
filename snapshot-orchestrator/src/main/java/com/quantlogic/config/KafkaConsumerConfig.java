package com.quantlogic.config;

import com.quantlogic.common.entity.EngineRegistrationMessage;
import com.quantlogic.common.message.MarkerAndAddressReservationMessage;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Value(value = "${kafka.bootstrapAddress}")
    private String bootstrapAddress;

    public ConsumerFactory<String, MarkerAndAddressReservationMessage> markerMessageKafkaConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "markerMessage");
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(MarkerAndAddressReservationMessage.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MarkerAndAddressReservationMessage> markerMessageKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, MarkerAndAddressReservationMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(markerMessageKafkaConsumerFactory());
        return factory;
    }

    public ConsumerFactory<String, EngineRegistrationMessage> engineRegistrationMessageConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "engineRegistration");
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(EngineRegistrationMessage.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, EngineRegistrationMessage> engineRegistrationMessageConcurrentKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, EngineRegistrationMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(engineRegistrationMessageConsumerFactory());
        return factory;
    }



}
