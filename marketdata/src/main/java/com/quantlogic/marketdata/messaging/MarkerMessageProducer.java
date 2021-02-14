package com.quantlogic.marketdata.messaging;

import com.quantlogic.common.message.MarkerAndAddressResevationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class MarkerMessageProducer {

    private final KafkaTemplate<String, MarkerAndAddressResevationMessage> kafkaTemplate;

    @Value(value = "${marker.topic}")
    private String markerTopic;

    private static final ThreadLocal<MarkerAndAddressResevationMessage> markerMessageThreadLocal = ThreadLocal.withInitial(MarkerAndAddressResevationMessage::new);

    public MarkerMessageProducer(@Autowired KafkaTemplate<String, MarkerAndAddressResevationMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMarker(long snapshotTime, String completeId, int version, boolean closeBucket){
        //id is of form Spot|*
        // of Vol|*
        String id = completeId.split("\\|")[0];
        MarkerAndAddressResevationMessage markerMessage = markerMessageThreadLocal.get();
        markerMessage.setId(completeId);
        markerMessage.setVersion(version);
        markerMessage.setCloseBucket(closeBucket);
        markerMessage.setSnapshotTime(snapshotTime);
        switch (id){
            case "Spot":
                kafkaTemplate.send(markerTopic, 0, markerMessage.getId(), markerMessage);
                break;
            case "Vol":
                kafkaTemplate.send(markerTopic, 1, markerMessage.getId(), markerMessage);
                break;
            case "YieldCurve":
                kafkaTemplate.send(markerTopic, 2, markerMessage.getId(), markerMessage);
                break;
        }
    }
}
