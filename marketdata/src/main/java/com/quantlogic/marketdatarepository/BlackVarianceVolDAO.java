package com.quantlogic.marketdatarepository;

import com.quantlogic.common.entity.CacheKey;
import com.quantlogic.common.entity.TimedBlackVarianceVolatility;
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
public class BlackVarianceVolDAO implements MarketDataDAO<CacheKey, TimedBlackVarianceVolatility>{

    private final RedisTemplate<String, TimedBlackVarianceVolatility> redisTemplate;
    private final MarkerMessageProducer markerMessageProducer;
    private static final Logger LOGGER = LoggerFactory.getLogger(BlackVarianceVolDAO.class);

    public BlackVarianceVolDAO(@Autowired RedisTemplate<String, TimedBlackVarianceVolatility> redisTemplate,
                               @Autowired MarkerMessageProducer markerMessageProducer) {
        this.redisTemplate = redisTemplate;
        this.markerMessageProducer = markerMessageProducer;
    }

    @Override
    public void save(CacheKey key, TimedBlackVarianceVolatility value) {
        HashOperations<String, CacheKey, TimedBlackVarianceVolatility> hashOps = this.redisTemplate.opsForHash();
        hashOps.put("VOLS", key, value);
        LOGGER.info("Saved TimedBlackVarianceVolatility {} with key {} ", value, key);
        TimedBlackVarianceVolatility vol = hashOps.get("VOLS", key);
        LOGGER.info("RETRIEVED Saved VOL {} with key {} ", vol, key);
        this.markerMessageProducer.sendMarker(value.getSnapshotTime(), value.getName(), value.getVersion(), false);
    }

    @Override
    public TimedBlackVarianceVolatility get(CacheKey key) {
        return this.redisTemplate.opsForValue().get(key);
    }
}
