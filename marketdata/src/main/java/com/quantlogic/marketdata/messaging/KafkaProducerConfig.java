package com.quantlogic.marketdata.messaging;

import com.quantlogic.common.message.MarkerAndAddressResevationMessage;
import com.quantlogic.dto.BlackVarianceVolatilityDTO;
import com.quantlogic.dto.SpotPriceDTO;
import com.quantlogic.dto.VanillaOptionDTO;
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
    public ProducerFactory<String, SpotPriceDTO> spotPriceProducerFactory() {
        Map<String, Object> configProps = getStringObjectMap();
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, SpotPriceDTO> spotPriceKafkaTemplate() {
        return new KafkaTemplate<>(spotPriceProducerFactory());
    }

    @Bean
    public ProducerFactory<String, BlackVarianceVolatilityDTO> blackVolVarianceProducerFactory() {
        Map<String, Object> configProps = getStringObjectMap();
        return new DefaultKafkaProducerFactory<>(configProps);
    }
    @Bean
    public KafkaTemplate<String, BlackVarianceVolatilityDTO> blackVolVarianceKafkaTemplate() {
        return new KafkaTemplate<>(blackVolVarianceProducerFactory());
    }

    @Bean
    public ProducerFactory<String, VanillaOptionDTO> vanillaOptionsProducerFactory() {
        Map<String, Object> configProps = getStringObjectMap();
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public ProducerFactory<String, MarkerAndAddressResevationMessage> markerMessageProducerFactory() {
        Map<String, Object> configProps = getStringObjectMap();
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, VanillaOptionDTO> vanillaOptionKafkaTemplate() {
        return new KafkaTemplate<>(vanillaOptionsProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, MarkerAndAddressResevationMessage> markerMessageKafkaTemplate() {
        return new KafkaTemplate<>(markerMessageProducerFactory());
    }

    private Map<String, Object> getStringObjectMap() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return configProps;
    }


}
