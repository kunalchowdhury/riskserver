package com.quantlogic.rediscache.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
@ComponentScan(basePackages = "com.quantlogic")
public class RuleRepositoryImpl implements RuleRepository {

    private final RedisTemplate<String, String> template;

    public RuleRepositoryImpl(@Autowired RedisTemplate<String, String> stringRedisTemplate) {
        this.template = stringRedisTemplate;
        System.out.println(this.template);
    }

    @Override
    public void saveRule(String ruleSet, String rule){
       this.template.opsForList().rightPush(ruleSet , rule);
    }

    @Override
    public void deleteRule(String ruleSet){
        this.template.delete(ruleSet);
    }

    @Override
    public Collection<String> getRuleSet(String ruleSet) {
        return this.template.opsForList().range(ruleSet,0, Integer.MAX_VALUE);
    }
}
