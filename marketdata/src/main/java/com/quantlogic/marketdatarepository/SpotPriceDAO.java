package com.quantlogic.marketdatarepository;

import com.quantlogic.common.entity.CacheKey;
import com.quantlogic.common.entity.SpotPrice;
import com.quantlogic.marketdata.messaging.MarkerMessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@ComponentScan(basePackages = "com.quantlogic")
@ConfigurationPropertiesScan("com.quantlogic")
@EnableJpaRepositories(basePackages = "com.quantlogic")
@Repository
public class SpotPriceDAO implements MarketDataDAO<CacheKey, SpotPrice>{

    private final RedisTemplate<String, SpotPrice> redisTemplate;
    private final MarkerMessageProducer markerMessageProducer;
    private static final Logger LOGGER = LoggerFactory.getLogger(SpotPriceDAO.class);

    public SpotPriceDAO(@Autowired RedisTemplate<String, SpotPrice> redisTemplate,
                        @Autowired MarkerMessageProducer markerMessageProducer) {
        this.redisTemplate = redisTemplate;
        this.markerMessageProducer = markerMessageProducer;
    }

    @Override
    public void save(CacheKey key, SpotPrice value) {
        HashOperations<String, CacheKey, SpotPrice> hashOps = this.redisTemplate.opsForHash();
        hashOps.put("SPOTS", key, value);
        LOGGER.info("Saved Spot {} with key {} ", value, key);
        this.markerMessageProducer.sendMarker(value.getSnapshotTime(), value.getName(), value.getVersion(), true);

    }

    @Override
    public SpotPrice get(CacheKey key) {
        return this.redisTemplate.opsForValue().get(key);
    }
}
