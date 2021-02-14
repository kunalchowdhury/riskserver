package com.quantlogic.messaging;

import com.quantlogic.common.entity.EngineRegistrationMessage;
import com.quantlogic.common.message.MarkerAndAddressResevationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import rx.subjects.PublishSubject;

@Component
public class ParameterMarkerConsumer {

    private final PublishSubject<MarkerAndAddressResevationMessage> publishSubject;
    private final EngineRegMessageConsumer engineRegMessageConsumer;

    public ParameterMarkerConsumer(@Autowired ParameterMessageConsumer parameterMessageConsumer,
                                   @Autowired EngineRegMessageConsumer engineRegMessageConsumer) {
        this.publishSubject = parameterMessageConsumer.getPublishSubject();
        this.engineRegMessageConsumer = engineRegMessageConsumer;
    }

    @KafkaListener(topicPartitions = @TopicPartition(topic = "${marker.topic}" , partitions = { "0"} ), containerFactory = "markerMessageKafkaListenerContainerFactory")
    public void markerListenerPartition0(MarkerAndAddressResevationMessage markerMessage, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
        System.out.println("Received MarkerMessage message: " + markerMessage + " from partition " + partition);
        publishSubject.onNext(markerMessage);
    }

    @KafkaListener(topicPartitions = @TopicPartition(topic = "${marker.topic}" , partitions = { "1"} ), containerFactory = "markerMessageKafkaListenerContainerFactory")
    public void markerListenerPartition1(MarkerAndAddressResevationMessage markerMessage, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
        System.out.println("Received MarkerMessage message: " + markerMessage + " from partition " + partition);
        publishSubject.onNext(markerMessage);
    }

    @KafkaListener(topicPartitions = @TopicPartition(topic = "${marker.topic}" , partitions = { "2"} ), containerFactory = "markerMessageKafkaListenerContainerFactory")
    public void markerListenerPartition2(MarkerAndAddressResevationMessage markerMessage, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
        System.out.println("Received MarkerMessage message: " + markerMessage + " from partition " + partition);
        publishSubject.onNext(markerMessage);
    }

    @KafkaListener(topics = "${address.reservation.topic}", groupId = "engineRegistration", containerFactory = "engineRegistrationMessageConcurrentKafkaListenerContainerFactory")
    public void listenEngineReservationMessage(EngineRegistrationMessage message) {
        System.out.println("Received EngineRegistrationMessage in group 'engineRegistration': " + message);
        engineRegMessageConsumer.exec(message);
    }
}
