package com.quantlogic.marketdata;

import com.quantlogic.common.entity.CacheKey;
import com.quantlogic.common.entity.SpotPrice;
import com.quantlogic.common.entity.TimedVanillaOption;
import com.quantlogic.dto.SpotPriceDTO;
import com.quantlogic.dto.VanillaOptionDTO;
import com.quantlogic.marketdata.messaging.MarketDataProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

//@SpringBootApplication
public class MarketDataSender {
    @Autowired
    private RedisTemplate<String, SpotPrice> redisTemplate;

    @Autowired
    private RedisTemplate<String, TimedVanillaOption> vanillaOptionRedisTemplate;


    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(MarketDataSender.class, args);
        MarketDataProducer marketDataProducer = context.getBean(MarketDataProducer.class);
        MarketDataSender marketDataSender = context.getBean(MarketDataSender.class);

        SpotPriceDTO spotPriceDTO = new SpotPriceDTO("AAPL", 90.4, 91.5, 20.6, 19, 19.5, 10);
        marketDataProducer.sendMessageToPartition(MarketDataProducer.MarketDataEntityType.SPOT,  spotPriceDTO );

        VanillaOptionDTO vanillaOptionDTO = new VanillaOptionDTO(30.5, "AAPL2M", 40.0, 45.0, 48.0, System.currentTimeMillis(),
                System.currentTimeMillis(), (byte)2, (byte)2, (byte)3, "AAPL", 1, 1,  "AAPL2M" );
        marketDataProducer.sendMessageToPartition(MarketDataProducer.MarketDataEntityType.VANILLAOPTION,  vanillaOptionDTO );
        /* System.out.println("saved record -> " +marketDataSender.redisTemplate.opsForHash().get("SPOTS", new CacheKey(2, "AAPL")));
        System.out.println("saved record -> " +marketDataSender.redisTemplate.opsForHash().get("SPOTS", new CacheKey(3, "AAPL")));
        System.out.println("saved record -> " +marketDataSender.redisTemplate.opsForHash().get("SPOTS", new CacheKey(4, "AAPL")));
        System.out.println("saved record -> " +marketDataSender.redisTemplate.opsForHash().get("SPOTS", new CacheKey(5, "AAPL")));
        System.out.println("saved record -> " +marketDataSender.redisTemplate.opsForHash().get("SPOTS", new CacheKey(6, "AAPL")));*/
        System.out.println("saved record -> " +marketDataSender.redisTemplate.opsForHash().get("SPOTS", new CacheKey(9, "AAPL")));
        System.out.println("saved record -> " +marketDataSender.vanillaOptionRedisTemplate.opsForHash().get("INSTRUMENTS", new CacheKey(1, "AAPL2M")));



    }
}
