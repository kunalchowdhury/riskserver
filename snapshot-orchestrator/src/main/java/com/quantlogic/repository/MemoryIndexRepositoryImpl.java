package com.quantlogic.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

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
    public Collection<Long> getMemoryAddresses(String paramKey) {
        return Objects.requireNonNull(this.template.opsForList().
                range(paramKey, 0, Integer.MAX_VALUE))
                .stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }
}
