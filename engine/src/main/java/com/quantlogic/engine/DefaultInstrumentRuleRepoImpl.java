package com.quantlogic.engine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DefaultInstrumentRuleRepoImpl implements InstrumentRuleRepository{
    private final RedisTemplate<String, String> redisTemplate;

    public DefaultInstrumentRuleRepoImpl(@Autowired RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String getPrimarySpotKey(String primaryInstrument, String secondaryInstrument) {
        return (String) redisTemplate.opsForHash().get("spots_rule",primaryInstrument);
    }

    @Override
    public String getVolSurfaceKey(String primaryInstrument, String secondaryInstrument) {
        return (String) redisTemplate.opsForHash().get("vols_rule",primaryInstrument);
    }

    @Override
    public String getYieldCurveKey(String primaryInstrument, String secondaryInstrument) {
        return (String) redisTemplate.opsForHash().get("yc_rule",primaryInstrument);
    }
}
