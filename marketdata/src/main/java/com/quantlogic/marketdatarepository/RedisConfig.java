package com.quantlogic.marketdatarepository;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import com.quantlogic.common.entity.CacheKey;
import com.quantlogic.common.entity.SpotPrice;
import com.quantlogic.common.entity.TimedBlackVarianceVolatility;
import com.quantlogic.common.entity.TimedVanillaOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisConfig {

    @Value(value = "${spring.redis.password}")
    private String redisPassword;

    @Value(value = "${spring.redis.port}")
    private int redisPort;

    @Value(value = "${spring.redis.host}")
    private String redisHost;
    @Bean
    public RedisPassword getRedisPassword(){
        return RedisPassword.of(redisPassword);
    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration server = new RedisStandaloneConfiguration(redisHost, redisPort);
        server.setPassword(getRedisPassword());
        return new LettuceConnectionFactory(server);
    }

   @Bean(name = "spotPriceTemplate")
    public RedisTemplate<CacheKey, SpotPrice> customSpotPriceTemplate(@Autowired RedisConnectionFactory factory) {
        RedisTemplate<CacheKey, SpotPrice> template = new RedisTemplate<>();
        template.setKeySerializer(new FastJsonRedisSerializer<>(CacheKey.class));
        template.setValueSerializer(new SpotPriceRedisSerializer());
        template.setConnectionFactory(factory);
        template.afterPropertiesSet();
        return template;
    }

    @Bean(name = "blackVarianceVolPriceTemplate")
    public RedisTemplate<CacheKey, TimedBlackVarianceVolatility> customBlackVarianceVolTemplate(@Autowired RedisConnectionFactory factory) {
        RedisTemplate<CacheKey, TimedBlackVarianceVolatility> template = new RedisTemplate<>();
        template.setKeySerializer(new FastJsonRedisSerializer<>(CacheKey.class));
        template.setValueSerializer(new BlackVarianceVolRedisSerializer());
        template.setConnectionFactory(factory);
        template.afterPropertiesSet();
        return template;
    }

    @Bean(name = "vanillaOptionTemplate")
    public RedisTemplate<CacheKey, TimedVanillaOption> customVanillaOptionTemplate(@Autowired RedisConnectionFactory factory) {
        RedisTemplate<CacheKey, TimedVanillaOption> template = new RedisTemplate<>();
        template.setKeySerializer(new FastJsonRedisSerializer<>(CacheKey.class));
        template.setValueSerializer(new VanillaOptionsRedisSerializer());
        template.setConnectionFactory(factory);
        template.afterPropertiesSet();
        return template;
    }
}
