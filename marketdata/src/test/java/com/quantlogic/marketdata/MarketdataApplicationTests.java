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

        SpotPriceDTO spotPriceDTO = new SpotPriceDTO("AAPL", 90.4, 91.5, 20.6, 19, 19.5, 10);
        marketDataProducer.sendMessageToPartition(MarketDataProducer.MarketDataEntityType.SPOT,  spotPriceDTO );

        VanillaOptionDTO vanillaOptionDTO = new VanillaOptionDTO(30.5, "AAPL2M", 40.0, 45.0, 48.0, System.currentTimeMillis(),
                System.currentTimeMillis(), (byte)2, (byte)2, (byte)3, "AAPL", 1, 1,  "AAPL2M" );
        marketDataProducer.sendMessageToPartition(MarketDataProducer.MarketDataEntityType.VANILLAOPTION,  vanillaOptionDTO );

        long l = System.currentTimeMillis();
        long[] expiratons = new long[]{System.currentTimeMillis(), System.currentTimeMillis()};
        double[] strikes = new double[]{20.0, 21.0};
        double[][] vols = new double[][]{{0.4, 0.5}, {0.24, 0.53}};

        BlackVarianceVolatilityDTO blackVarianceVolatilityDTO = new BlackVarianceVolatilityDTO(l, (byte)1, expiratons,  strikes, (byte)1, vols, 1, 1, "AAPL|BLACKVOL" );
        marketDataProducer.sendMessageToPartition(MarketDataProducer.MarketDataEntityType.BLACKVOL,  blackVarianceVolatilityDTO );

        Object spot = marketDataSender.redisTemplate.opsForHash().get("SPOTS", new CacheKey(10, "AAPL"));
        Assertions.assertEquals(new SpotPrice(spotPriceDTO), spot);

        Object vanillaOption = marketDataSender.vanillaOptionRedisTemplate.opsForHash().get("INSTRUMENTS", new CacheKey(1, "AAPL2M"));
        Assertions.assertEquals(vanillaOption, new TimedVanillaOption(vanillaOptionDTO));

        Object blackVol = marketDataSender.blackVarianceVolatilityRedisTemplate.opsForHash().get("VOLS", new CacheKey(1, "AAPL|BLACKVOL"));
        Assertions.assertEquals(blackVol, new TimedBlackVarianceVolatility(blackVarianceVolatilityDTO));
    }

}
