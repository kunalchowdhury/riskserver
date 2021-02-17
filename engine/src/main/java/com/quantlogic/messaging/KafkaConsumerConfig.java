package com.quantlogic.messaging;

import com.quantlogic.common.entity.SnapshotAllocationMessage;
import com.quantlogic.common.message.MarkerAndAddressReservationMessage;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {
    @Value(value = "${kafka.bootstrapAddress}")
    private String bootstrapAddress;

    public ConsumerFactory<String, SnapshotAllocationMessage> stringSnapshotAllocationMessageConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "snapshotallocmsg");
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(SnapshotAllocationMessage.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SnapshotAllocationMessage> snapshotAllocationMessageConcurrentKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, SnapshotAllocationMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(stringSnapshotAllocationMessageConsumerFactory());
        return factory;
    }

    public ConsumerFactory<String, MarkerAndAddressReservationMessage> markerAndAddressReservationMessageConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "markeraddresvmsg");
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(MarkerAndAddressReservationMessage.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MarkerAndAddressReservationMessage> messageConcurrentKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, MarkerAndAddressReservationMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(markerAndAddressReservationMessageConsumerFactory());
        return factory;
    }
}
