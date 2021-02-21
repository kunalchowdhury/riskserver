package com.quantlogic.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
@ComponentScan(basePackages = "com.quantlogic")
public class MemoryIndexRepositoryImpl implements MemoryIndexRepository{

    private final RedisTemplate<String, String> template;
    public MemoryIndexRepositoryImpl(@Autowired RedisTemplate<String, String> template) {
        this.template = template;
    }

    @Override
    public void mapMemoryAddress(String paramKey, String memoryAddress) {
        this.template.opsForList().leftPush(paramKey, memoryAddress);
    }

    @Override
    public void unmapMemoryAddress(String paramKey) {
        this.template.delete(paramKey);
    }

    @Override
    public List<String> getMemoryAddresses(String paramKey) {
        return new ArrayList<>(Objects.requireNonNull(this.template.opsForList().
                range(paramKey, 0, Integer.MAX_VALUE)));
    }
}
