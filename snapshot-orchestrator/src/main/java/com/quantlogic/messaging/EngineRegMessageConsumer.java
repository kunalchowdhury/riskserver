package com.quantlogic.messaging;

import com.quantlogic.common.entity.EngineRegistrationMessage;
import com.quantlogic.common.entity.SnapshotAllocationMessage;
import com.quantlogic.config.EngineConfig;
import com.quantlogic.mmap.MemoryMapManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
            long curr = add;
            add += step;
            return curr;
        }
    }
    private final CountDownLatch latch;
    private final Set<EngineRegistrationMessage> messageSet;
    private final MemoryMapManager memoryMapManager;
    private final RedisTemplate<String, String> template;
    private final Map<String, String> rootAddressEngineTopicMap;

    public EngineRegMessageConsumer(@Autowired MemoryMapManager memoryMapManager,
                                    @Autowired RedisTemplate<String, String> template,
                                    @Autowired SnapshotAllocationMessageProducer snapshotAllocationMessageProducer,
                                    @Autowired EngineConfig engineConfig) {
        this.latch = new CountDownLatch(engineConfig.getEngineCount());
        this.messageSet = new HashSet<>();
        this.memoryMapManager = memoryMapManager;
        this.template = template;
        this.snapshotAllocationMessageProducer = snapshotAllocationMessageProducer;
        this.rootAddressEngineTopicMap = new HashMap<String, String>();
    }
    public void exec(EngineRegistrationMessage registrationMessage){
        messageSet.add(registrationMessage);
        this.latch.countDown();
        int[] engineCounter = new int[]{0};
        messageSet.forEach(engineRegistrationMessage -> {
            String spotids = registrationMessage.getSpotids();
            String volIds = registrationMessage.getVolIds();
            String yieldCurveIds = registrationMessage.getYieldCurveIds();
            String cacheId = engineRegistrationMessage.getPid()
                    + "_" + engineRegistrationMessage.getHostId();
            Long sz = this.memoryMapManager.reserveMemory(cacheId,
                    (StringUtils.isNotEmpty(spotids)) ? spotids.split(",").length : 0,
                    (StringUtils.isNotEmpty(volIds)) ? volIds.split(",").length : 0 ,
                    (StringUtils.isNotEmpty(yieldCurveIds)) ? yieldCurveIds.split(",").length : 0);
            LOGGER.info("Root address for Pid{} and HostId{} is  {} ",
                    engineRegistrationMessage.getPid(), engineRegistrationMessage.getHostId(), sz );

            MutableLong mutableLong = new MutableLong(0);
            if(spotids != null) {
                Arrays.stream(spotids.split(",")).forEach(id -> {
                    template.opsForList().leftPush(id, cacheId+ ":"+ mutableLong.inc(4));
                });
            }
            if(volIds != null) {
                Arrays.stream(volIds.split(",")).forEach(id -> {
                    template.opsForList().leftPush(id, cacheId+ ":"+ mutableLong.inc(4));
                });
            }
            if(yieldCurveIds != null) {
                Arrays.stream(yieldCurveIds.split(",")).forEach(id -> {
                    template.opsForList().leftPush(id, cacheId+ ":"+ mutableLong.inc(4));
                });
            }
            SnapshotAllocationMessage snapshotAllocationMessage = new SnapshotAllocationMessage();
            snapshotAllocationMessage.setMappedFile(this.memoryMapManager.getMappedFileName(cacheId));
            snapshotAllocationMessage.setSize(sz);
            snapshotAllocationMessage.setCacheId(cacheId);
            snapshotAllocationMessage.setDone(false);
            snapshotAllocationMessage.setSpotIds(spotids);
            snapshotAllocationMessage.setVolIds(volIds);
            snapshotAllocationMessage.setYieldCurveIds(registrationMessage.getYieldCurveIds());
            snapshotAllocationMessage.setCorrelationId(registrationMessage.getId());

            String topic = "engine_" + registrationMessage.getEngineSequence() + "_init_registration";
            snapshotAllocationMessageProducer.sendAllocationMessage(snapshotAllocationMessage, topic);
            rootAddressEngineTopicMap.put(cacheId, topic);
            LOGGER.info("Reservation message reply sent for Pid {} and Host {} to topic {}",  engineRegistrationMessage.getPid(),
                    engineRegistrationMessage.getHostId(), topic);
            engineCounter[0]++;
        });
    }

    public String getTopic(String cacheId) {
        return rootAddressEngineTopicMap.get(cacheId);
    }

    public boolean waitTillInitialized(){
        LOGGER.info("Waiting to receive reservation messages requests from {} engine(s) ",  this.latch.getCount());
        try {
            this.latch.await();
            LOGGER.info("Engine registration completed. ");
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException("Should not have reached here");

    }


}
