package com.quantlogic.marketdata.messaging;

import com.quantlogic.common.message.MarkerAndAddressReservationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class MarkerMessageProducer {

    private final Logger LOGGER = LoggerFactory.getLogger(MarkerMessageProducer.class);
    private final KafkaTemplate<String, MarkerAndAddressReservationMessage> kafkaTemplate;

    @Value(value = "${marker.topic}")
    private String markerTopic;

    private static final ThreadLocal<MarkerAndAddressReservationMessage> markerMessageThreadLocal = ThreadLocal.withInitial(MarkerAndAddressReservationMessage::new);

    public MarkerMessageProducer(@Autowired KafkaTemplate<String, MarkerAndAddressReservationMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMarker(long snapshotTime, String completeId, int version, boolean closeBucket){
        //id is of form Spot|*
        // of Vol|*
        String id = completeId.split("\\|")[0];
        MarkerAndAddressReservationMessage markerMessage = markerMessageThreadLocal.get();
        markerMessage.setId(completeId);
        markerMessage.setVersion(version);
        markerMessage.setCloseBucket(closeBucket);
        markerMessage.setSnapshotTime(snapshotTime);
        switch (id){
            case "Spot":
                kafkaTemplate.send(markerTopic, 0, markerMessage.getId(), markerMessage);
                LOGGER.info("Sent marker message {} on parition 0", markerMessage);
                break;
            case "Vol":
                kafkaTemplate.send(markerTopic, 1, markerMessage.getId(), markerMessage);
                LOGGER.info("Sent marker message {} on parition 1", markerMessage);
                break;
            case "YieldCurve":
                kafkaTemplate.send(markerTopic, 2, markerMessage.getId(), markerMessage);
                LOGGER.info("Sent marker message {} on parition 2", markerMessage);
                break;
        }
    }
}
