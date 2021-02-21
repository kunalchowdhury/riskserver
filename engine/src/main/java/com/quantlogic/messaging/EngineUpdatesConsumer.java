package com.quantlogic.messaging;

import com.quantlogic.common.entity.SnapshotAllocationMessage;
import com.quantlogic.engine.ValuationOrchestrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Configuration
public class EngineUpdatesConsumer {

    private final Logger logger = LoggerFactory.getLogger(EngineUpdatesConsumer.class);

    private final ValuationOrchestrator valuationOrchestrator;

    public EngineUpdatesConsumer(@Autowired ValuationOrchestrator valuationOrchestrator) {
        this.valuationOrchestrator = valuationOrchestrator;
    }

    @KafkaListener(topics = "${engine.allocation.response}", groupId = "engineRegistrationMessage",
            containerFactory = "snapshotAllocationMessageConcurrentKafkaListenerContainerFactory")
    public void listenEngineReservationMessage(SnapshotAllocationMessage message) {
        logger.info("Received EngineRegistrationMessage in group 'engineRegistrationMessage': {}" , message);
        valuationOrchestrator.processSnapshotAllocationMessage(message);
    }

}
