package com.quantlogic.valuation.entity;

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

import java.util.Arrays;

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
    public RedisTemplate<String, SpotPrice> customSpotPriceTemplate(@Autowired RedisConnectionFactory factory) {
        RedisTemplate<String, SpotPrice> template = new RedisTemplate<>();
        template.setDefaultSerializer(new KryoSerializer<>(Arrays.asList(CacheKey.class, SpotPrice.class)));
        template.setConnectionFactory(factory);
        return template;
    }

    @Bean(name = "blackVarianceVolPriceTemplate")
    public RedisTemplate<String, TimedBlackVarianceVolatility> customBlackVarianceVolTemplate(@Autowired RedisConnectionFactory factory) {
        RedisTemplate<String, TimedBlackVarianceVolatility> template = new RedisTemplate<>();
        template.setDefaultSerializer(new KryoSerializer<>(Arrays.asList(CacheKey.class, TimedBlackVarianceVolatility.class,
                long[].class, long[][].class, double[].class, double[][].class)));
        template.setConnectionFactory(factory);
        return template;
    }

    @Bean(name = "vanillaOptionTemplate")
    public RedisTemplate<String, TimedVanillaOption> customVanillaOptionTemplate(@Autowired RedisConnectionFactory factory) {
        RedisTemplate<String, TimedVanillaOption> template = new RedisTemplate<>();
        template.setDefaultSerializer(new KryoSerializer<>(Arrays.asList(CacheKey.class, TimedVanillaOption.class,
                long[].class, long[][].class, double[].class, double[][].class)));
        template.setConnectionFactory(factory);
        return template;
    }
}
