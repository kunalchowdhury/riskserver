package com.quantlogic.messaging;

import com.quantlogic.common.entity.EngineRegistrationMessage;
import com.quantlogic.common.entity.SnapshotAllocationMessage;
import com.quantlogic.mmap.MemoryMapManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CountDownLatch;

@Component
@ComponentScan(basePackages = "com.quantlogic")
@EnableJpaRepositories
@Configuration
public class EngineRegMessageConsumer {

    private final Logger LOGGER = LoggerFactory.getLogger(EngineRegMessageConsumer.class);
    private final SnapshotAllocationMessageProducer snapshotAllocationMessageProducer;

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
    private final Map<Long, String> rootAddressEngineTopicMap;

    @Value(value = "${engine.count}")
    private int engineCount;

    public EngineRegMessageConsumer(@Autowired MemoryMapManager memoryMapManager,
                                    @Autowired RedisTemplate<String, String> template,
                                    @Autowired SnapshotAllocationMessageProducer snapshotAllocationMessageProducer) {
        this.latch = new CountDownLatch(engineCount);
        this.messageSet = new HashSet<>();
        this.memoryMapManager = memoryMapManager;
        this.template = template;
        this.snapshotAllocationMessageProducer = snapshotAllocationMessageProducer;
        this.rootAddressEngineTopicMap = new HashMap<>();
    }
    public void exec(EngineRegistrationMessage registrationMessage){
        messageSet.add(registrationMessage);
        this.latch.countDown();
        int[] engineCounter = new int[]{0};
        messageSet.forEach(engineRegistrationMessage -> {
            Long address = this.memoryMapManager.reserveMemory(engineRegistrationMessage.getPid()
                    +"_"+engineRegistrationMessage.getHostId(),
                    registrationMessage.getSpotids().split(",").length,
                    registrationMessage.getVolIds().split(",").length,
                    registrationMessage.getYieldCurveIds().split(",").length);
            LOGGER.info("Root address for Pid{} and HostId{} is  {} ",
                    engineRegistrationMessage.getPid(), engineRegistrationMessage.getHostId(), address );
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
            SnapshotAllocationMessage snapshotAllocationMessage = new SnapshotAllocationMessage();
            snapshotAllocationMessage.setStartMemAddress(address);
            snapshotAllocationMessage.setDone(false);
            snapshotAllocationMessageProducer.sendAllocationMessage(snapshotAllocationMessage,

                    "engine_"+engineCounter[0]+"_init_registration");
            rootAddressEngineTopicMap.put(address, "engine_"+engineCounter[0]+"_process_message");
            LOGGER.info("Reservation message reply sent for Pid {} and Host {}",  engineRegistrationMessage.getPid(),
                    engineRegistrationMessage.getHostId());
            engineCounter[0]++;
        });
    }

    public String getTopic(long address) {
        return rootAddressEngineTopicMap.get(address);
    }
}
