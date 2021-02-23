package com.quantlogic.messaging;

import com.google.common.collect.Maps;
import com.quantlogic.common.entity.EngineRegistrationMessage;
import com.quantlogic.common.message.MarkerAndAddressReservationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

@Configuration
@Component
public class EngineRegistrationMessageProducer {

    private final KafkaTemplate<String, MarkerAndAddressReservationMessage> messageKafkaTemplate;
    @Value(value = "${address.reservation.topic}")
    private String engineRegistrationMessageTopic;

    private final KafkaTemplate<String, EngineRegistrationMessage> kafkaTemplate;
    private final Map<String, CountDownLatch> latch;

    private static final Logger LOGGER = LoggerFactory.getLogger(EngineRegistrationMessageProducer.class);
    private static final ThreadLocal<MarkerAndAddressReservationMessage> threadLocal = ThreadLocal.withInitial(MarkerAndAddressReservationMessage::new);

    public EngineRegistrationMessageProducer(@Autowired KafkaTemplate<String, EngineRegistrationMessage> kafkaTemplate,
                                             @Autowired KafkaTemplate<String, MarkerAndAddressReservationMessage> messageKafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.latch = Maps.newConcurrentMap();
        this.messageKafkaTemplate = messageKafkaTemplate;
    }

    public void sendFreeAddressMarker(String cacheId, String topic, int idx){
        MarkerAndAddressReservationMessage markerAndAddressReservationMessage = threadLocal.get();
        markerAndAddressReservationMessage.setFreeAddress(true);
        markerAndAddressReservationMessage.setId(cacheId);
        markerAndAddressReservationMessage.setAddressLoc(idx);
        messageKafkaTemplate.send(topic, Math.abs(cacheId.hashCode() % 3), cacheId, markerAndAddressReservationMessage);
        LOGGER.info(" ----- Sent Free Address Message for CacheId {}", cacheId);
    }

    public void sendEngineRegistrationMessage(EngineRegistrationMessage engineRegistrationMessage){
        kafkaTemplate.send(engineRegistrationMessageTopic, engineRegistrationMessage);
        this.latch.put(engineRegistrationMessage.getId(), new CountDownLatch(1));
        LOGGER.info("----- Waiting to hear back from orchestrator for address confirmation for id {} ----- ",
                engineRegistrationMessage.getId());
        try {
            this.latch.get(engineRegistrationMessage.getId()).await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void receivedResponse(String id){
        LOGGER.info("----- Received Respoonse for registration {} ----- ",id );
        this.latch.get(id).countDown();
    }

}
