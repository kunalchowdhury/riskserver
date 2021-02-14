package com.quantlogic.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.concurrent.Executors;

@Configuration
public class RedisConfig {
    @Value(value = "${spring.redis.password}")
    private String redisPassword;

    @Value(value = "${spring.redis.port}")
    private int redisPort;

    @Value(value = "${spring.redis.host}")
    private String redisHost;

    @Bean
    @Primary
    public RedisTemplate<String, String> redisTemplate() {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

   @Bean
   public LettuceConnectionFactory redisConnectionFactory() {
       RedisStandaloneConfiguration server = new RedisStandaloneConfiguration(redisHost, redisPort);
       server.setPassword(redisPassword);
       return new LettuceConnectionFactory(server);
   }
    @Bean
    MessageListenerAdapter messageListener() {
        return new MessageListenerAdapter((MessageListener) (message, bytes) -> {
            //in redis-cli : config set notify-keyspace-events KEA
            System.out.println("Got message for ..."+message);
        });
    }

    @Bean(name = "cacheManager")
    @Primary
    public RedisCacheManager redisCacheManager(LettuceConnectionFactory jedisConnectionFactory)
    {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues()
                //.entryTtl(Duration.ofSeconds(10))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));

        redisCacheConfiguration.usePrefix();

        return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(jedisConnectionFactory)
                .cacheDefaults(redisCacheConfiguration).build();

    }

    @Bean
    RedisMessageListenerContainer redisContainer(LettuceConnectionFactory lettuceConnectionFactory) {
        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(lettuceConnectionFactory);
        container.addMessageListener(messageListener(), new PatternTopic("__keyevent@*:*"));
        container.setTaskExecutor(Executors.newFixedThreadPool(4));
        container.afterPropertiesSet();
        return container;
    }


}
