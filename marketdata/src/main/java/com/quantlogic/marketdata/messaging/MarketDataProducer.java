package com.quantlogic.marketdata.messaging;

import com.quantlogic.dto.BlackVarianceVolatilityDTO;
import com.quantlogic.dto.DTOEntity;
import com.quantlogic.dto.SpotPriceDTO;
import com.quantlogic.dto.VanillaOptionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class MarketDataProducer {

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
                System.out.println("about to send spot");
        //        spotPriceDTOKafkaTemplate.send(spotPriceTopicName, (SpotPriceDTO)message );
                spotPriceDTOKafkaTemplate.send(spotPriceTopicName, hash % spotPartitionCount, name, (SpotPriceDTO) message);
                System.out.println("DONE.");
                break;
            case BLACKVOL:
                blackVarianceVolatilityDTOKafkaTemplate.send(blackVarianceVolTopicName, hash % blackVarianceVolPartitionCount, name, (BlackVarianceVolatilityDTO) message);
                break;
            case VANILLAOPTION:
                vanillaOptionDTOKafkaTemplate.send(vanillaOptionTopicName, hash % vanillaOptionPartitionCount, name, (VanillaOptionDTO) message);
                break;
        }

    }


}
