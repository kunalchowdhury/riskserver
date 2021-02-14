package com.quantlogic.messaging;

import com.quantlogic.common.entity.EngineRegistrationMessage;
import com.quantlogic.mmap.MemoryMapManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

@Component
@ComponentScan(basePackages = "com.quantlogic")
@EnableJpaRepositories
public class EngineRegMessageConsumer {

    static class MutableLong{
        private long add;

        MutableLong(long add) {
            this.add = add;
        }
        long inc(long step){
            add += step;
            return add;
        }
    }
    private final CountDownLatch latch;
    private final Set<EngineRegistrationMessage> messageSet;
    private final MemoryMapManager memoryMapManager;
    private final RedisTemplate<String, String> template;

    @Value(value = "${engine.count}")
    private int engineCount;

    public EngineRegMessageConsumer(@Autowired MemoryMapManager memoryMapManager,
                                    @Autowired RedisTemplate<String, String> template) {
        this.latch = new CountDownLatch(engineCount);
        this.messageSet = new HashSet<>();
        this.memoryMapManager = memoryMapManager;
        this.template = template;
    }
    public void exec(EngineRegistrationMessage registrationMessage){
        messageSet.add(registrationMessage);
        this.latch.countDown();
        messageSet.forEach(engineRegistrationMessage -> {
            Long address = this.memoryMapManager.reserveMemory(engineRegistrationMessage.getPid()
                    +"+"+engineRegistrationMessage.getHostId(),
                    registrationMessage.getSpotids().split(",").length,
                    registrationMessage.getVolIds().split(",").length,
                    registrationMessage.getYieldCurveIds().split(",").length);
            MutableLong mutableLong = new MutableLong(address);
            Arrays.stream(registrationMessage.getSpotids().split(",")).forEach(id -> {
                template.opsForList().leftPush(id, String.valueOf(mutableLong.inc(4)));
            });
            Arrays.stream(registrationMessage.getVolIds().split(",")).forEach(id -> {
                template.opsForList().leftPush(id, String.valueOf(mutableLong.inc(4)));
            });
            Arrays.stream(registrationMessage.getYieldCurveIds().split(",")).forEach(id -> {
                template.opsForList().leftPush(id, String.valueOf(mutableLong.inc(4)));
            });
        });


    }
}
