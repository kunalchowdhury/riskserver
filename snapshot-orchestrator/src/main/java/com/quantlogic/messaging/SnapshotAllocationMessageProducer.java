package com.quantlogic.messaging;

import com.quantlogic.common.entity.SnapshotAllocationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class SnapshotAllocationMessageProducer {

    private final KafkaTemplate<String, SnapshotAllocationMessage> kafkaTemplate;

    public SnapshotAllocationMessageProducer(@Autowired KafkaTemplate<String, SnapshotAllocationMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    //first time sent to engines to commence processing - memory location is returned
    public void sendAllocationMessage(SnapshotAllocationMessage snapshotAllocationMessage, String topic){
        this.kafkaTemplate.send(topic, snapshotAllocationMessage);
    }

}
