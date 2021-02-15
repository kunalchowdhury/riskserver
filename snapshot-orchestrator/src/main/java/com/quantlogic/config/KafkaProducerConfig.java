package com.quantlogic.config;

import com.quantlogic.common.entity.SnapshotAllocationMessage;
import com.quantlogic.common.message.MarkerAndAddressReservationMessage;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {
    @Value(value = "${kafka.bootstrapAddress}")
    private String bootstrapAddress;

    @Bean
    public ProducerFactory<String, SnapshotAllocationMessage> snapshotAllocationMessageProducerFactory() {
        Map<String, Object> configProps = getStringObjectMap();
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, SnapshotAllocationMessage> snapshotAllocationMessageKafkaTemplate() {
        return new KafkaTemplate<>(snapshotAllocationMessageProducerFactory());
    }

    @Bean
    public ProducerFactory<String, MarkerAndAddressReservationMessage> markerAndAddressReservationMessageProducerFactory() {
        Map<String, Object> configProps = getStringObjectMap();
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, MarkerAndAddressReservationMessage> markerAndAddressReservationMessageKafkaTemplate() {
        return new KafkaTemplate<>(markerAndAddressReservationMessageProducerFactory());
    }

    private Map<String, Object> getStringObjectMap() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return configProps;
    }

}
