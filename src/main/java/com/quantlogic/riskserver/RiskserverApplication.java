package com.quantlogic.riskserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootApplication
@PropertySource("classpath:application.properties")
@ConfigurationPropertiesScan("com.quantlogic")
@EnableJpaRepositories(basePackages = "com.quantlogic")
public class RiskserverApplication {

    @Autowired
    private RedisTemplate<String, String> template;
    public static void main(String[] args) {
        SpringApplication.run(RiskserverApplication.class, args);
    }

}
