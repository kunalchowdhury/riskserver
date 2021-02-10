package com.quantlogic.marketdata.messaging;

import com.quantlogic.dto.BlackVarianceVolatilityDTO;
import com.quantlogic.dto.SpotPriceDTO;
import com.quantlogic.dto.VanillaOptionDTO;
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

    public ConsumerFactory<String, BlackVarianceVolatilityDTO> blackVarianceVolConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "blackvariancevol");
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(BlackVarianceVolatilityDTO.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, BlackVarianceVolatilityDTO> blackVarianceVolKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, BlackVarianceVolatilityDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(blackVarianceVolConsumerFactory());
        return factory;
    }

    public ConsumerFactory<String, SpotPriceDTO> spotPriceConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "spotprice");
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(SpotPriceDTO.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SpotPriceDTO> spotPriceKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, SpotPriceDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(spotPriceConsumerFactory());
        return factory;
    }


    public ConsumerFactory<String, VanillaOptionDTO> vanillaOptionConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "vanillaoption");
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(VanillaOptionDTO.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, VanillaOptionDTO> vanillaOptionKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, VanillaOptionDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(vanillaOptionConsumerFactory());
        return factory;
    }
}
