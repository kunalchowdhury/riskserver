package com.quantlogic.marketdata.messaging;

import com.quantlogic.common.entity.CacheKey;
import com.quantlogic.common.entity.SpotPrice;
import com.quantlogic.common.entity.TimedBlackVarianceVolatility;
import com.quantlogic.common.entity.TimedVanillaOption;
import com.quantlogic.dto.BlackVarianceVolatilityDTO;
import com.quantlogic.dto.SpotPriceDTO;
import com.quantlogic.dto.VanillaOptionDTO;
import com.quantlogic.marketdatarepository.BlackVarianceVolDAO;
import com.quantlogic.marketdatarepository.SpotPriceDAO;
import com.quantlogic.marketdatarepository.VanillaOptionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class MarketDataConsumer {

    private final BlackVarianceVolDAO blackVarianceVolDAO;

    private final SpotPriceDAO spotPriceDAO;

    private final VanillaOptionDAO vanillaOptionDAO;

    public MarketDataConsumer(@Autowired BlackVarianceVolDAO blackVarianceVolDAO,
                              @Autowired SpotPriceDAO spotPriceDAO,
                              @Autowired VanillaOptionDAO vanillaOptionDAO) {
        this.blackVarianceVolDAO = blackVarianceVolDAO;
        this.spotPriceDAO = spotPriceDAO;
        this.vanillaOptionDAO = vanillaOptionDAO;
    }

    private void saveBlackVarianceVol(BlackVarianceVolatilityDTO blackVarianceVolatilityDTO){
        TimedBlackVarianceVolatility timedBlackVarianceVolatility = new TimedBlackVarianceVolatility(blackVarianceVolatilityDTO);
        long snapshotTime = System.currentTimeMillis();
        timedBlackVarianceVolatility.setSnapshotTime(snapshotTime);
        this.blackVarianceVolDAO.save(new CacheKey(blackVarianceVolatilityDTO.getVersion(), blackVarianceVolatilityDTO.getName()), timedBlackVarianceVolatility);
    }

    private void saveSpotPrice(SpotPriceDTO spotPriceDTO){
        SpotPrice spotPrice = new SpotPrice(spotPriceDTO);
        long snapshotTime = System.currentTimeMillis();
        spotPrice.setSnapshotTime(snapshotTime);
        this.spotPriceDAO.save(new CacheKey(spotPriceDTO.getVersion(), spotPriceDTO.getName()), spotPrice);
    }

    private void saveVanillaOption(VanillaOptionDTO vanillaOptionDTO){
        TimedVanillaOption timedVanillaOption = new TimedVanillaOption(vanillaOptionDTO);
        long snapshotTime = System.currentTimeMillis();
        timedVanillaOption.setSnapshotTime(snapshotTime);
        this.vanillaOptionDAO.save(new CacheKey(vanillaOptionDTO.getVersion(), vanillaOptionDTO.getName()), timedVanillaOption);
    }

    @KafkaListener(topicPartitions = @TopicPartition(topic = "${spot.topic.name}" , partitions = { "0"} ), containerFactory = "spotPriceKafkaListenerContainerFactory")
    public void spotPriceListenerPartition0(SpotPriceDTO spotPriceDTO, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
        System.out.println("Received spot message: " + spotPriceDTO+ " from partition "+partition);
        saveSpotPrice(spotPriceDTO);

    }

    @KafkaListener(topicPartitions = @TopicPartition(topic = "${spot.topic.name}" , partitions = { "1"} ), containerFactory = "spotPriceKafkaListenerContainerFactory")
    public void spotPriceListenerPartition1(SpotPriceDTO spotPriceDTO, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
        System.out.println("Received spot message: " + spotPriceDTO+ " from partition "+partition);
        saveSpotPrice(spotPriceDTO);
    }

    @KafkaListener(topicPartitions = @TopicPartition(topic = "${spot.topic.name}" , partitions = { "2"} ), containerFactory = "spotPriceKafkaListenerContainerFactory")
    public void spotPriceListenerPartition2(SpotPriceDTO spotPriceDTO, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
        System.out.println("Received spot message: " + spotPriceDTO+ " from partition "+partition);
        saveSpotPrice(spotPriceDTO);
    }

    @KafkaListener(topicPartitions = @TopicPartition(topic = "${vol.topic.name}" , partitions = { "0"} ), containerFactory = "blackVarianceVolKafkaListenerContainerFactory")
    public void blackVarianceVolListenerPartition0(BlackVarianceVolatilityDTO blackVarianceVolatilityDTO, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
        System.out.println("Received blackVarianceVolatilityDTO message: " + blackVarianceVolatilityDTO+ " from partition "+partition);
        saveBlackVarianceVol(blackVarianceVolatilityDTO);
    }

    @KafkaListener(topicPartitions = @TopicPartition(topic = "${vol.topic.name}" , partitions = { "1"} ), containerFactory = "blackVarianceVolKafkaListenerContainerFactory")
    public void blackVarianceVolListenerPartition1(BlackVarianceVolatilityDTO blackVarianceVolatilityDTO, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
        System.out.println("Received blackVarianceVolatilityDTO message: " + blackVarianceVolatilityDTO+ " from partition "+partition);
        saveBlackVarianceVol(blackVarianceVolatilityDTO);
    }

    @KafkaListener(topicPartitions = @TopicPartition(topic = "${vol.topic.name}" , partitions = { "2"} ), containerFactory = "blackVarianceVolKafkaListenerContainerFactory")
    public void blackVarianceVolListenerPartition2(BlackVarianceVolatilityDTO blackVarianceVolatilityDTO, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
        System.out.println("Received blackVarianceVolatilityDTO message: " + blackVarianceVolatilityDTO+ " from partition "+partition);
        saveBlackVarianceVol(blackVarianceVolatilityDTO);
    }

    @KafkaListener(topicPartitions = @TopicPartition(topic = "${vanilla.option.topic.name}" , partitions = { "0"} ), containerFactory = "vanillaOptionKafkaListenerContainerFactory")
    public void vanillaOptionListenerPartition0(VanillaOptionDTO vanillaOptionDTO, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
        System.out.println("Received vanillaOptionDTO message: " + vanillaOptionDTO+ " from partition "+partition);
        saveVanillaOption(vanillaOptionDTO);
    }

    @KafkaListener(topicPartitions = @TopicPartition(topic = "${vanilla.option.topic.name}" , partitions = { "1"} ), containerFactory = "vanillaOptionKafkaListenerContainerFactory")
    public void vanillaOptionListenerPartition1(VanillaOptionDTO vanillaOptionDTO, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
        System.out.println("Received vanillaOptionDTO message: " + vanillaOptionDTO+ " from partition "+partition);
        saveVanillaOption(vanillaOptionDTO);
    }

    @KafkaListener(topicPartitions = @TopicPartition(topic = "${vanilla.option.topic.name}" , partitions = { "2"} ), containerFactory = "vanillaOptionKafkaListenerContainerFactory")
    public void vanillaOptionVolListenerPartition2(VanillaOptionDTO vanillaOptionDTO, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
        System.out.println("Received vanillaOptionDTO message: " + vanillaOptionDTO+ " from partition "+partition);
        saveVanillaOption(vanillaOptionDTO);
    }



}
