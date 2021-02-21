package com.quantlogic.marketdata.messaging;

import com.quantlogic.dto.BlackVarianceVolatilityDTO;
import com.quantlogic.dto.DTOEntity;
import com.quantlogic.dto.SpotPriceDTO;
import com.quantlogic.dto.VanillaOptionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class MarketDataProducer {

    private final Logger LOGGER = LoggerFactory.getLogger(MarketDataProducer.class);
    public MarketDataProducer(@Autowired KafkaTemplate<String, SpotPriceDTO> spotPriceDTOKafkaTemplate,
                              @Autowired KafkaTemplate<String, BlackVarianceVolatilityDTO> blackVarianceVolatilityDTOKafkaTemplate,
                              @Autowired KafkaTemplate<String, VanillaOptionDTO> vanillaOptionDTOKafkaTemplate) {
        this.spotPriceDTOKafkaTemplate = spotPriceDTOKafkaTemplate;
        this.blackVarianceVolatilityDTOKafkaTemplate = blackVarianceVolatilityDTOKafkaTemplate;
        this.vanillaOptionDTOKafkaTemplate = vanillaOptionDTOKafkaTemplate;
    }

    public enum MarketDataEntityType{ SPOT, BLACKVOL, VANILLAOPTION}

    private final KafkaTemplate<String, SpotPriceDTO> spotPriceDTOKafkaTemplate;

    private final KafkaTemplate<String, BlackVarianceVolatilityDTO> blackVarianceVolatilityDTOKafkaTemplate;

    private final KafkaTemplate<String, VanillaOptionDTO> vanillaOptionDTOKafkaTemplate;

    @Value(value = "${spot.topic.name}")
    private String spotPriceTopicName;

    @Value(value = "${vol.topic.name}")
    private String blackVarianceVolTopicName;

    @Value(value = "${vanilla.option.topic.name}")
    private String vanillaOptionTopicName;

    @Value(value = "${spot.topic.parititioncount}")
    private int spotPartitionCount;

    @Value(value = "${vol.topic.partitioncount}")
    private int blackVarianceVolPartitionCount;

    @Value(value = "${vanillaoption.topic.partitioncount}")
    private int vanillaOptionPartitionCount;


    public <T extends DTOEntity> void sendMessageToPartition(MarketDataEntityType type, T message) {
        String name = message.getName();
        int hash = message.getName().hashCode();
        switch (type){
            case SPOT:
                spotPriceDTOKafkaTemplate.send(spotPriceTopicName, Math.abs(hash % spotPartitionCount) , name, (SpotPriceDTO) message);
                LOGGER.info("Sent {} message to partition {} ", message, hash % spotPartitionCount);
                break;
            case BLACKVOL:
                blackVarianceVolatilityDTOKafkaTemplate.send(blackVarianceVolTopicName, Math.abs(hash % blackVarianceVolPartitionCount), name, (BlackVarianceVolatilityDTO) message);
                LOGGER.info("Sent {} message to partition {} ", message, hash % blackVarianceVolPartitionCount);
                break;
            case VANILLAOPTION:
                vanillaOptionDTOKafkaTemplate.send(vanillaOptionTopicName, Math.abs(hash % vanillaOptionPartitionCount), name, (VanillaOptionDTO) message);
                LOGGER.info("Sent {} message to partition {} ", message, hash % vanillaOptionPartitionCount);
                break;
        }

    }

    public void setSpotPriceTopicName(String spotPriceTopicName) {
        this.spotPriceTopicName = spotPriceTopicName;
    }

    public void setBlackVarianceVolTopicName(String blackVarianceVolTopicName) {
        this.blackVarianceVolTopicName = blackVarianceVolTopicName;
    }

    public void setVanillaOptionTopicName(String vanillaOptionTopicName) {
        this.vanillaOptionTopicName = vanillaOptionTopicName;
    }

    public void setSpotPartitionCount(int spotPartitionCount) {
        this.spotPartitionCount = spotPartitionCount;
    }

    public void setBlackVarianceVolPartitionCount(int blackVarianceVolPartitionCount) {
        this.blackVarianceVolPartitionCount = blackVarianceVolPartitionCount;
    }

    public void setVanillaOptionPartitionCount(int vanillaOptionPartitionCount) {
        this.vanillaOptionPartitionCount = vanillaOptionPartitionCount;
    }
}
