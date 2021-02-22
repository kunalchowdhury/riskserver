package com.quantlogic.marketdata;

import com.quantlogic.common.entity.CacheKey;
import com.quantlogic.common.entity.SpotPrice;
import com.quantlogic.common.entity.TimedBlackVarianceVolatility;
import com.quantlogic.common.entity.TimedVanillaOption;
import com.quantlogic.dto.BlackVarianceVolatilityDTO;
import com.quantlogic.dto.SpotPriceDTO;
import com.quantlogic.dto.VanillaOptionDTO;
import com.quantlogic.marketdata.messaging.MarketDataProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@ComponentScan(basePackages = "com.quantlogic")
@ConfigurationPropertiesScan("com.quantlogic")
@EnableJpaRepositories(basePackages = "com.quantlogic")
@Configuration
//@SpringBootTest
@SpringBootApplication
class MarketdataApplicationTests {
    @Autowired
    private RedisTemplate<String, SpotPrice> redisTemplate;

    @Autowired
    private RedisTemplate<String, TimedVanillaOption> vanillaOptionRedisTemplate;

    @Autowired
    private RedisTemplate<String, TimedBlackVarianceVolatility> blackVarianceVolatilityRedisTemplate;

    public static void main(String[] args) throws InterruptedException {
         testMarketDataMessagingAndPersistence(true);
    }

    /*
    * public MarketDataProducer(@Autowired KafkaTemplate<String, SpotPriceDTO> spotPriceDTOKafkaTemplate,
                              @Autowired KafkaTemplate<String, BlackVarianceVolatilityDTO> blackVarianceVolatilityDTOKafkaTemplate,
                              @Autowired KafkaTemplate<String, VanillaOptionDTO> vanillaOptionDTOKafkaTemplate) {
    * */
    private static void testMarketDataMessagingAndPersistence(boolean b) throws InterruptedException {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        MarketDataProducer marketDataProducer = new MarketDataProducer(
                new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(configProps)),
                new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(configProps)),
                new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(configProps)));
        marketDataProducer.setBlackVarianceVolPartitionCount(3);
        marketDataProducer.setSpotPartitionCount(3);
        marketDataProducer.setVanillaOptionPartitionCount(3);
        marketDataProducer.setSpotPriceTopicName("spot-prices");
        marketDataProducer.setBlackVarianceVolTopicName("vols");
        marketDataProducer.setVanillaOptionTopicName("instruments");
        SpotPriceDTO spotPriceDTO = new SpotPriceDTO("Spot|AAPL", 1650.1, 1652.0, 1652.0, 1651.0, 1652.5, 25);
        marketDataProducer.sendMessageToPartition(MarketDataProducer.MarketDataEntityType.SPOT, spotPriceDTO);
        Thread.sleep(100000);
        if(b){
            return;

        }

        Calendar calendar = Calendar.getInstance();

        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calendar.set(java.util.Calendar.MINUTE, 0);
        calendar.set(java.util.Calendar.SECOND, 0);
        calendar.set(java.util.Calendar.MILLISECOND, 0);


        calendar.set(Calendar.DAY_OF_MONTH, 23);
        calendar.set(Calendar.MONTH, 1);
        calendar.set(Calendar.YEAR, 2021);
        long settlementDate = calendar.getTimeInMillis();

        calendar.set(Calendar.DAY_OF_MONTH, 23);
        calendar.set(Calendar.MONTH, 1);
        calendar.set(Calendar.YEAR, 2022);
        long maturity = calendar.getTimeInMillis();


        VanillaOptionDTO vanillaOptionDTO =
                new VanillaOptionDTO(1656.0,
                        "AAPL",
                        0.06,
                        0.00,
                        0.2,
                        settlementDate,
                        maturity, (byte)2, (byte)1, (byte)1, "AAPL", 1, 21,  "AAPL2M" );
        marketDataProducer.sendMessageToPartition(MarketDataProducer.MarketDataEntityType.VANILLAOPTION,  vanillaOptionDTO );


        long[] expiratons = new long[5];

        calendar.set(Calendar.DAY_OF_MONTH,  19);
        calendar.set(Calendar.MONTH,  11);
        calendar.set(Calendar.YEAR,  2026);

        expiratons[0] = calendar.getTimeInMillis();

        calendar.set(Calendar.DAY_OF_MONTH,  16);
        calendar.set(Calendar.MONTH,  0);
        calendar.set(Calendar.YEAR,  2027);

        expiratons[1] = calendar.getTimeInMillis();

        calendar.set(Calendar.DAY_OF_MONTH,  20);
        calendar.set(Calendar.MONTH,  2);
        calendar.set(Calendar.YEAR,  2027);

        expiratons[2] = calendar.getTimeInMillis();

        calendar.set(Calendar.DAY_OF_MONTH,  19);
        calendar.set(Calendar.MONTH,  5);
        calendar.set(Calendar.YEAR,  2027);

        expiratons[3] = calendar.getTimeInMillis();

        calendar.set(Calendar.DAY_OF_MONTH,  18);
        calendar.set(Calendar.MONTH,  8);
        calendar.set(Calendar.YEAR,  2027);

        expiratons[4] = calendar.getTimeInMillis();


        double[] strikes = new double[]{1650.1, 1660.0, 1670.0, 1675.0, 1680.0};
        double[][] vols = new double[][]{
                {0.15640,0.15433,0.16079,0.16394,0.17383},
                {0.15343,0.15240,0.15804,0.16255,0.17303},
                {0.15128,0.14888,0.15512,0.15944,0.17038},
                {0.14798,0.14906,0.15522,0.16171,0.16156},
                {0.14580,0.14576,0.15364,0.16037,0.16042}
        };

        calendar.set(Calendar.DAY_OF_MONTH,  22);
        calendar.set(Calendar.MONTH,  1);
        calendar.set(Calendar.YEAR,  2021);
        long l = calendar.getTimeInMillis();


        BlackVarianceVolatilityDTO blackVarianceVolatilityDTO = new BlackVarianceVolatilityDTO(l, (byte)1, expiratons,  strikes, (byte)2, vols, 21, 1, "Vol|AAPL|BLACKVOL_TEST" );
        marketDataProducer.sendMessageToPartition(MarketDataProducer.MarketDataEntityType.BLACKVOL,  blackVarianceVolatilityDTO );

        Thread.sleep(100000);
    }

    //@Test
    static void testMarketDataMessagingAndPersistence1(boolean spotUpdatesOnly) {

        ConfigurableApplicationContext context = SpringApplication.run(MarketdataApplicationTests.class);
        MarketDataProducer marketDataProducer = context.getBean(MarketDataProducer.class);
        MarketdataApplicationTests marketDataSender = context.getBean(MarketdataApplicationTests.class);
        SpotPriceDTO spotPriceDTO = new SpotPriceDTO("Spot|AAPL", 1652.0, 1652.0, 1652.0, 1651.0, 1652.5, 12);
        marketDataProducer.sendMessageToPartition(MarketDataProducer.MarketDataEntityType.SPOT, spotPriceDTO);
        if(spotUpdatesOnly) {
            return;
        }

        Calendar calendar = Calendar.getInstance();

        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calendar.set(java.util.Calendar.MINUTE, 0);
        calendar.set(java.util.Calendar.SECOND, 0);
        calendar.set(java.util.Calendar.MILLISECOND, 0);


        calendar.set(Calendar.DAY_OF_MONTH, 23);
        calendar.set(Calendar.MONTH, 1);
        calendar.set(Calendar.YEAR, 2021);
        long settlementDate = calendar.getTimeInMillis();

        calendar.set(Calendar.DAY_OF_MONTH, 23);
        calendar.set(Calendar.MONTH, 1);
        calendar.set(Calendar.YEAR, 2022);
        long maturity = calendar.getTimeInMillis();


        VanillaOptionDTO vanillaOptionDTO =
                new VanillaOptionDTO(1656.0,
                        "AAPL",
                        0.06,
                        0.00,
                        0.2,
                        settlementDate,
                        maturity, (byte)2, (byte)1, (byte)1, "AAPL", 1, 12,  "AAPL2M" );
        marketDataProducer.sendMessageToPartition(MarketDataProducer.MarketDataEntityType.VANILLAOPTION,  vanillaOptionDTO );


        long[] expiratons = new long[5];

        calendar.set(Calendar.DAY_OF_MONTH,  19);
        calendar.set(Calendar.MONTH,  11);
        calendar.set(Calendar.YEAR,  2026);

        expiratons[0] = calendar.getTimeInMillis();

        calendar.set(Calendar.DAY_OF_MONTH,  16);
        calendar.set(Calendar.MONTH,  0);
        calendar.set(Calendar.YEAR,  2027);

        expiratons[1] = calendar.getTimeInMillis();

        calendar.set(Calendar.DAY_OF_MONTH,  20);
        calendar.set(Calendar.MONTH,  2);
        calendar.set(Calendar.YEAR,  2027);

        expiratons[2] = calendar.getTimeInMillis();

        calendar.set(Calendar.DAY_OF_MONTH,  19);
        calendar.set(Calendar.MONTH,  5);
        calendar.set(Calendar.YEAR,  2027);

        expiratons[3] = calendar.getTimeInMillis();

        calendar.set(Calendar.DAY_OF_MONTH,  18);
        calendar.set(Calendar.MONTH,  8);
        calendar.set(Calendar.YEAR,  2027);

        expiratons[4] = calendar.getTimeInMillis();


        double[] strikes = new double[]{1650.0, 1660.0, 1670.0, 1675.0, 1680.0};
        double[][] vols = new double[][]{
                {0.15640,0.15433,0.16079,0.16394,0.17383},
                {0.15343,0.15240,0.15804,0.16255,0.17303},
                {0.15128,0.14888,0.15512,0.15944,0.17038},
                {0.14798,0.14906,0.15522,0.16171,0.16156},
                {0.14580,0.14576,0.15364,0.16037,0.16042}
        };

        calendar.set(Calendar.DAY_OF_MONTH,  22);
        calendar.set(Calendar.MONTH,  1);
        calendar.set(Calendar.YEAR,  2021);
        long l = calendar.getTimeInMillis();


        BlackVarianceVolatilityDTO blackVarianceVolatilityDTO = new BlackVarianceVolatilityDTO(l, (byte)1, expiratons,  strikes, (byte)2, vols, 12, 1, "Vol|AAPL|BLACKVOL_TEST" );
        marketDataProducer.sendMessageToPartition(MarketDataProducer.MarketDataEntityType.BLACKVOL,  blackVarianceVolatilityDTO );

        Object spot = marketDataSender.redisTemplate.opsForHash().get("SPOTS", new CacheKey(12, "Spot|AAPL"));
        Assertions.assertTrue(spot instanceof SpotPrice);
        Assertions.assertEquals(((SpotPrice) spot).getTicker(), spotPriceDTO.getTicker());
        Assertions.assertEquals(((SpotPrice) spot).getMid(), spotPriceDTO.getMid());
        Assertions.assertEquals(((SpotPrice) spot).getHi(), spotPriceDTO.getHi());
        Assertions.assertEquals(((SpotPrice) spot).getLo(), spotPriceDTO.getLo());
        Assertions.assertEquals(((SpotPrice) spot).getOpen(), spotPriceDTO.getOpen());
        Assertions.assertEquals(((SpotPrice) spot).getClose(), spotPriceDTO.getClose());


        Object vanillaOption = marketDataSender.vanillaOptionRedisTemplate.opsForHash().get("INSTRUMENTS", new CacheKey(12, "AAPL2M"));
        Assertions.assertTrue(vanillaOption instanceof TimedVanillaOption);
        Assertions.assertEquals(((TimedVanillaOption) vanillaOption).getStrike(), vanillaOptionDTO.getStrike());
        Assertions.assertEquals(((TimedVanillaOption) vanillaOption).getUnderlying(), vanillaOptionDTO.getUnderlying());
        Assertions.assertEquals(((TimedVanillaOption) vanillaOption).getRiskFreeRate(), vanillaOptionDTO.getRiskFreeRate());
        Assertions.assertEquals(((TimedVanillaOption) vanillaOption).getDividendYield(), vanillaOptionDTO.getDividendYield());
        Assertions.assertEquals(((TimedVanillaOption) vanillaOption).getVolatility(), vanillaOptionDTO.getVolatility());
        Assertions.assertEquals(((TimedVanillaOption) vanillaOption).getSettlementDate(), vanillaOptionDTO.getSettlementDate());
        Assertions.assertEquals(((TimedVanillaOption) vanillaOption).getMaturity(), vanillaOptionDTO.getMaturity());
        Assertions.assertEquals(((TimedVanillaOption) vanillaOption).getDayCount(), vanillaOptionDTO.getDayCount());
        Assertions.assertEquals(((TimedVanillaOption) vanillaOption).getOptionType(), vanillaOptionDTO.getOptionType());
        Assertions.assertEquals(((TimedVanillaOption) vanillaOption).getExcerciseType(), vanillaOptionDTO.getExcerciseType());
        Assertions.assertEquals(((TimedVanillaOption) vanillaOption).getTickerSymbol(), vanillaOptionDTO.getTickerSymbol());
        Assertions.assertEquals(((TimedVanillaOption) vanillaOption).getShardId(), vanillaOptionDTO.getShardId());
        Assertions.assertEquals(((TimedVanillaOption) vanillaOption).getVersion(), vanillaOptionDTO.getVersion());
        Assertions.assertEquals(((TimedVanillaOption) vanillaOption).getName(), vanillaOptionDTO.getName());

        Object blackVol = marketDataSender.blackVarianceVolatilityRedisTemplate.opsForHash().get("VOLS", new CacheKey(12, "Vol|AAPL|BLACKVOL_TEST"));
        Assertions.assertTrue(blackVol instanceof TimedBlackVarianceVolatility);
        Assertions.assertEquals(blackVarianceVolatilityDTO.getValuationDate(), ((TimedBlackVarianceVolatility) blackVol).getValuationDate());
        Assertions.assertEquals(blackVarianceVolatilityDTO.getCalendar(), ((TimedBlackVarianceVolatility) blackVol).getCalendar());
        Assertions.assertArrayEquals(blackVarianceVolatilityDTO.getExpirations(), ((TimedBlackVarianceVolatility) blackVol).getExpirations());
        Assertions.assertArrayEquals(blackVarianceVolatilityDTO.getStrikes(), ((TimedBlackVarianceVolatility) blackVol).getStrikes());
        Assertions.assertEquals(blackVarianceVolatilityDTO.getCurDayCounter(), ((TimedBlackVarianceVolatility) blackVol).getCurDayCounter());
        Assertions.assertTrue(Objects.deepEquals(blackVarianceVolatilityDTO.getVols(), ((TimedBlackVarianceVolatility) blackVol).getVols()));
        Assertions.assertEquals(blackVarianceVolatilityDTO.getVersion(), ((TimedBlackVarianceVolatility) blackVol).getVersion());
        Assertions.assertEquals(blackVarianceVolatilityDTO.getShardId(), ((TimedBlackVarianceVolatility) blackVol).getShardId());
        Assertions.assertEquals(blackVarianceVolatilityDTO.getName(), ((TimedBlackVarianceVolatility) blackVol).getName());


        try {
            Thread.sleep(5000);
        //    context.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
