package com.quantlogic.marketdata.messaging;

import com.quantlogic.dto.SpotPriceDTO;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;

public class MarketDataConsumer {

    @KafkaListener(topicPartitions = @TopicPartition(topic = "${spot.topic.name}" , partitions = { "0"} ), containerFactory = "spotPriceKafkaListenerContainerFactory")
    public void spotPriceListenerPartition0(SpotPriceDTO spotPriceDTO, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
        System.out.println("Received greeting message: " + spotPriceDTO+ " from partition "+partition);

    }

    @KafkaListener(topicPartitions = @TopicPartition(topic = "${spot.topic.name}" , partitions = { "1"} ), containerFactory = "spotPriceKafkaListenerContainerFactory")
    public void spotPriceListenerPartition1(SpotPriceDTO spotPriceDTO, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
        System.out.println("Received greeting message: " + spotPriceDTO+ " from partition "+partition);

    }

    @KafkaListener(topicPartitions = @TopicPartition(topic = "${spot.topic.name}" , partitions = { "2"} ), containerFactory = "spotPriceKafkaListenerContainerFactory")
    public void spotPriceListenerPartition2(SpotPriceDTO spotPriceDTO, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
        System.out.println("Received greeting message: " + spotPriceDTO+ " from partition "+partition);

    }

    @KafkaListener(topicPartitions = @TopicPartition(topic = "${vol.topic.name}" , partitions = { "0"} ), containerFactory = "blackVarianceVolConsumerFactory")
    public void blackVarianceVolListenerPartition0(SpotPriceDTO spotPriceDTO, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
        System.out.println("Received greeting message: " + spotPriceDTO+ " from partition "+partition);

    }

    @KafkaListener(topicPartitions = @TopicPartition(topic = "${vol.topic.name}" , partitions = { "1"} ), containerFactory = "blackVarianceVolConsumerFactory")
    public void blackVarianceVolListenerPartition1(SpotPriceDTO spotPriceDTO, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
        System.out.println("Received greeting message: " + spotPriceDTO+ " from partition "+partition);

    }

    @KafkaListener(topicPartitions = @TopicPartition(topic = "${vol.topic.name}" , partitions = { "2"} ), containerFactory = "blackVarianceVolConsumerFactory")
    public void blackVarianceVolListenerPartition2(SpotPriceDTO spotPriceDTO, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
        System.out.println("Received greeting message: " + spotPriceDTO+ " from partition "+partition);

    }

    @KafkaListener(topicPartitions = @TopicPartition(topic = "${vanilla.option.topic.name}" , partitions = { "0"} ), containerFactory = "vanillaOptionKafkaListenerContainerFactory")
    public void vanillaOptionListenerPartition0(SpotPriceDTO spotPriceDTO, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
        System.out.println("Received greeting message: " + spotPriceDTO+ " from partition "+partition);

    }

    @KafkaListener(topicPartitions = @TopicPartition(topic = "${vanilla.option.topic.name}" , partitions = { "1"} ), containerFactory = "vanillaOptionKafkaListenerContainerFactory")
    public void vanillaOptionListenerPartition1(SpotPriceDTO spotPriceDTO, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
        System.out.println("Received greeting message: " + spotPriceDTO+ " from partition "+partition);

    }

    @KafkaListener(topicPartitions = @TopicPartition(topic = "${vanilla.option.topic.name}" , partitions = { "2"} ), containerFactory = "vanillaOptionKafkaListenerContainerFactory")
    public void vanillaOptionVolListenerPartition2(SpotPriceDTO spotPriceDTO, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
        System.out.println("Received greeting message: " + spotPriceDTO+ " from partition "+partition);

    }



}
