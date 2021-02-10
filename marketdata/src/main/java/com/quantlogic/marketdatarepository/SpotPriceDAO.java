package com.quantlogic.marketdatarepository;

import com.quantlogic.common.entity.CacheKey;
import com.quantlogic.common.entity.SpotPrice;
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
public class SpotPriceDAO implements MarketDataDAO<CacheKey, SpotPrice>{

    private final RedisTemplate<CacheKey, SpotPrice> redisTemplate;

    public SpotPriceDAO(@Autowired RedisTemplate<CacheKey, SpotPrice> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void save(CacheKey key, SpotPrice value) {
        this.redisTemplate.opsForValue().set(key, value);

    }

    @Override
    public SpotPrice get(CacheKey key) {
        return this.redisTemplate.opsForValue().get(key);
    }
}
