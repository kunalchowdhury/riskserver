package com.quantlogic.messaging;

import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.quantlogic.common.entity.EngineRegistrationMessage;
import com.quantlogic.common.message.MarkerAndAddressReservationMessage;
import com.quantlogic.common.message.Watermark;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import rx.subjects.PublishSubject;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class ParameterMarkerConsumer {

    private final Logger LOGGER = LoggerFactory.getLogger(ParameterMarkerConsumer.class);
    private final PublishSubject<MarkerAndAddressReservationMessage> publishSubject;
    private final EngineRegMessageConsumer engineRegMessageConsumer;
    private final LinkedBlockingQueue<MarkerAndAddressReservationMessage> blockingQueue;
    private final WaterMarkEmittingStrategy[] waterMarkEmittingStrategy = new WaterMarkEmittingStrategy[]
            {WaterMarkEmittingStrategy.ALWAYS_TRUE};
    private volatile boolean enginesInitialized;

    public ParameterMarkerConsumer(@Autowired ParameterMessageConsumer parameterMessageConsumer,
                                   @Autowired EngineRegMessageConsumer engineRegMessageConsumer) {
        this.publishSubject = parameterMessageConsumer.getPublishSubject();
        this.engineRegMessageConsumer = engineRegMessageConsumer;
        this.blockingQueue = Queues.newLinkedBlockingQueue();
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        Set<MarkerAndAddressReservationMessage> set = Sets.newHashSet();
        this.enginesInitialized = false;
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            set.clear();
            try {
                if(!this.enginesInitialized) {
                    this.enginesInitialized = this.engineRegMessageConsumer.waitTillInitialized();
                }
                Queues.drain(blockingQueue, set, blockingQueue.size(), 10, TimeUnit.SECONDS);
                set.forEach(publishSubject::onNext);
                Arrays.stream(waterMarkEmittingStrategy).forEach(st -> {
                    if(st.shouldEmit()){
                        publishSubject.onNext(Watermark.INSTANCE);
                    }
                });

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, 1, 10, TimeUnit.SECONDS);

    }

    // RxJava best practice - never call onNext from multiple threads
    @KafkaListener(topicPartitions = @TopicPartition(topic = "${marker.topic}" , partitions = { "0"} ), containerFactory = "markerMessageKafkaListenerContainerFactory")
    public void markerListenerPartition0(MarkerAndAddressReservationMessage markerMessage, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
        LOGGER.info("Received MarkerMessage message: {} from partition {} " ,markerMessage, partition);
        putIntoQueue(markerMessage);
    }

    @KafkaListener(topicPartitions = @TopicPartition(topic = "${marker.topic}" , partitions = { "1"} ), containerFactory = "markerMessageKafkaListenerContainerFactory")
    public void markerListenerPartition1(MarkerAndAddressReservationMessage markerMessage, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
        LOGGER.info("Received MarkerMessage message: {} from partition {} " ,markerMessage, partition);
        putIntoQueue(markerMessage);
    }

    @KafkaListener(topicPartitions = @TopicPartition(topic = "${marker.topic}" , partitions = { "2"} ), containerFactory = "markerMessageKafkaListenerContainerFactory")
    public void markerListenerPartition2(MarkerAndAddressReservationMessage markerMessage, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
        LOGGER.info("Received MarkerMessage message: {} from partition {} " ,markerMessage, partition);
        putIntoQueue(markerMessage);
    }


    private void putIntoQueue(MarkerAndAddressReservationMessage markerMessage) {
        try {
            blockingQueue.put(markerMessage);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = "${address.reservation.topic}", groupId = "engineRegistration", containerFactory = "engineRegistrationMessageConcurrentKafkaListenerContainerFactory")
    public void listenEngineReservationMessage(EngineRegistrationMessage message) {
        LOGGER.info("Received EngineRegistrationMessage in group 'engineRegistration': {}" , message);
        engineRegMessageConsumer.exec(message);
    }
}
