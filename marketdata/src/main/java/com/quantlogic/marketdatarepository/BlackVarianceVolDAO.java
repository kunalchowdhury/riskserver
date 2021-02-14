package com.quantlogic.marketdatarepository;

import com.quantlogic.common.entity.CacheKey;
import com.quantlogic.common.entity.TimedBlackVarianceVolatility;
import com.quantlogic.marketdata.messaging.MarkerMessageProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@ComponentScan(basePackages = "com.quantlogic")
@ConfigurationPropertiesScan("com.quantlogic")
@EnableJpaRepositories(basePackages = "com.quantlogic")
@Repository
public class BlackVarianceVolDAO implements MarketDataDAO<CacheKey, TimedBlackVarianceVolatility>{

    private final RedisTemplate<CacheKey, TimedBlackVarianceVolatility> redisTemplate;
    private final MarkerMessageProducer markerMessageProducer;

    public BlackVarianceVolDAO(@Autowired RedisTemplate<CacheKey, TimedBlackVarianceVolatility> redisTemplate,
                               @Autowired MarkerMessageProducer markerMessageProducer) {
        this.redisTemplate = redisTemplate;
        this.markerMessageProducer = markerMessageProducer;
    }

    @Override
    public void save(CacheKey key, TimedBlackVarianceVolatility value) {
        this.redisTemplate.opsForValue().set(key, value);
        this.markerMessageProducer.sendMarker(value.getSnapshotTime(), value.getName(), value.getVersion(), false);
    }

    @Override
    public TimedBlackVarianceVolatility get(CacheKey key) {
        return this.redisTemplate.opsForValue().get(key);
    }
}
