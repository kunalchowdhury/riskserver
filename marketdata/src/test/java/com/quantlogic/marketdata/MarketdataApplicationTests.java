package com.quantlogic.marketdata;

import com.quantlogic.common.entity.CacheKey;
import com.quantlogic.common.entity.SpotPrice;
import com.quantlogic.common.entity.TimedBlackVarianceVolatility;
import com.quantlogic.common.entity.TimedVanillaOption;
import com.quantlogic.dto.BlackVarianceVolatilityDTO;
import com.quantlogic.dto.SpotPriceDTO;
import com.quantlogic.dto.VanillaOptionDTO;
import com.quantlogic.marketdata.messaging.MarketDataProducer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Objects;

@ComponentScan(basePackages = "com.quantlogic")
@ConfigurationPropertiesScan("com.quantlogic")
@EnableJpaRepositories(basePackages = "com.quantlogic")
@Configuration
@SpringBootTest
class MarketdataApplicationTests {
    @Autowired
    private RedisTemplate<String, SpotPrice> redisTemplate;

    @Autowired
    private RedisTemplate<String, TimedVanillaOption> vanillaOptionRedisTemplate;

    @Autowired
    private RedisTemplate<String, TimedBlackVarianceVolatility> blackVarianceVolatilityRedisTemplate;

    @Test
    void testMarketDataMessagingAndPersistence() {
        ConfigurableApplicationContext context = SpringApplication.run(MarketdataApplicationTests.class);
        MarketDataProducer marketDataProducer = context.getBean(MarketDataProducer.class);
        MarketdataApplicationTests marketDataSender = context.getBean(MarketdataApplicationTests.class);

        SpotPriceDTO spotPriceDTO = new SpotPriceDTO("Spot|AAPL", 90.4, 91.5, 20.6, 19, 19.5, 11);
        marketDataProducer.sendMessageToPartition(MarketDataProducer.MarketDataEntityType.SPOT,  spotPriceDTO );

        VanillaOptionDTO vanillaOptionDTO = new VanillaOptionDTO(30.5, "AAPL2M", 40.0, 45.0, 48.0, System.currentTimeMillis(),
                System.currentTimeMillis(), (byte)2, (byte)2, (byte)3, "AAPL", 1, 2,  "AAPL2M" );
        marketDataProducer.sendMessageToPartition(MarketDataProducer.MarketDataEntityType.VANILLAOPTION,  vanillaOptionDTO );

        long l = System.currentTimeMillis();
        long[] expiratons = new long[]{System.currentTimeMillis(), System.currentTimeMillis()};
        double[] strikes = new double[]{20.0, 21.0};
        double[][] vols = new double[][]{{0.4, 0.5}, {0.24, 0.53}};

        BlackVarianceVolatilityDTO blackVarianceVolatilityDTO = new BlackVarianceVolatilityDTO(l, (byte)1, expiratons,  strikes, (byte)1, vols, 4, 1, "Vol|AAPL|BLACKVOL_TEST" );
        marketDataProducer.sendMessageToPartition(MarketDataProducer.MarketDataEntityType.BLACKVOL,  blackVarianceVolatilityDTO );

        Object spot = marketDataSender.redisTemplate.opsForHash().get("SPOTS", new CacheKey(11, "Spot|AAPL"));
        Assertions.assertTrue(spot instanceof SpotPrice);
        Assertions.assertEquals(((SpotPrice) spot).getTicker(), spotPriceDTO.getTicker());
        Assertions.assertEquals(((SpotPrice) spot).getMid(), spotPriceDTO.getMid());
        Assertions.assertEquals(((SpotPrice) spot).getHi(), spotPriceDTO.getHi());
        Assertions.assertEquals(((SpotPrice) spot).getLo(), spotPriceDTO.getLo());
        Assertions.assertEquals(((SpotPrice) spot).getOpen(), spotPriceDTO.getOpen());
        Assertions.assertEquals(((SpotPrice) spot).getClose(), spotPriceDTO.getClose());


        Object vanillaOption = marketDataSender.vanillaOptionRedisTemplate.opsForHash().get("INSTRUMENTS", new CacheKey(2, "AAPL2M"));
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

        Object blackVol = marketDataSender.blackVarianceVolatilityRedisTemplate.opsForHash().get("VOLS", new CacheKey(4, "Vol|AAPL|BLACKVOL_TEST"));
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
            context.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
