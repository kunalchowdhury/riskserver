package com.quantlogic.marketdatarepository;

import com.quantlogic.common.entity.CacheKey;
import com.quantlogic.common.entity.TimedVanillaOption;
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
public class VanillaOptionDAO implements MarketDataDAO<CacheKey, TimedVanillaOption> {

    private final RedisTemplate<String, TimedVanillaOption> redisTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(VanillaOptionDAO.class);

    public VanillaOptionDAO(@Autowired RedisTemplate<String, TimedVanillaOption> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    @Override
    public void save(CacheKey key, TimedVanillaOption value) {
        HashOperations<String, CacheKey, TimedVanillaOption> hashOps = this.redisTemplate.opsForHash();
        hashOps.put("INSTRUMENTS", key, value);
        LOGGER.info("Saved TimedVanillaOption {} with key {} ", value, key);
        TimedVanillaOption option = hashOps.get("INSTRUMENTS", key);
        LOGGER.info("RETRIEVED Saved INSTRUMENTS {} with key {} ", option, key);
    }

    @Override
    public TimedVanillaOption get(CacheKey key) {
        return this.redisTemplate.opsForValue().get(key);
    }
}
