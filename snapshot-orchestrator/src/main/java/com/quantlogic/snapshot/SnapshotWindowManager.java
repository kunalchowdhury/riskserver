package com.quantlogic.snapshot;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.quantlogic.common.entity.SnapshotAllocationMessage;
import com.quantlogic.messaging.EngineRegMessageConsumer;
import com.quantlogic.messaging.SnapshotAllocationMessageProducer;
import com.quantlogic.mmap.MemoryMapManager;
import com.quantlogic.repository.MemoryIndexRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SnapshotWindowManager {

    static class BackUpSnapshot {
        String loc;
        int version;

        public BackUpSnapshot(String loc, int version) {
            this.loc = loc;
            this.version = version;
        }
    }

    private final MemoryMapManager memoryMapManager;
    private final Cache<String, List<BackUpSnapshot>> prevSnapshot;
    private final EngineRegMessageConsumer engineRegMessageConsumer;
    private final SnapshotAllocationMessageProducer snapshotAllocationMessageProducer;
    private final Map<String, SnapshotAllocationMessage> snapshotAllocationMessageMap;
    private final Map<String, SnapshotAllocationMessage> snapshotAllocationMessageMapTemp;
    private final Map<String, Integer> requestSentToEngines;
    private static final Logger LOGGER = LoggerFactory.getLogger(SnapshotWindowManager.class);

    public enum ParameterType {
        Spot, Vol, YieldCurve
    }

    private final Map<String, CompositeSnapshotWindow> taggedBucket;
    private final MemoryIndexRepository memoryIndexRepository;

    public SnapshotWindowManager(@Autowired MemoryIndexRepository memoryIndexRepository,
                                 @Autowired WindowConfig windowConfig,
                                 @Autowired MemoryMapManager memoryMapManager,
                                 @Autowired EngineRegMessageConsumer engineRegMessageConsumer,
                                 @Autowired SnapshotAllocationMessageProducer snapshotAllocationMessageProducer) {
        this.taggedBucket = new HashMap<>();
        String tags = windowConfig.getTags();
        this.memoryIndexRepository = memoryIndexRepository;
        this.memoryMapManager = memoryMapManager;
        this.prevSnapshot = CacheBuilder.newBuilder().build();
        this.engineRegMessageConsumer = engineRegMessageConsumer;
        this.snapshotAllocationMessageProducer = snapshotAllocationMessageProducer;
        this.snapshotAllocationMessageMap = Maps.newConcurrentMap();
        this.snapshotAllocationMessageMapTemp = Maps.newHashMap();
        this.requestSentToEngines = Maps.newConcurrentMap();
        Arrays.stream(tags.split(",")).forEach(tag -> {
            taggedBucket.put(tag, new CompositeSnapshotWindow());
            this.prevSnapshot.put(tag, Lists.newArrayList());
        });

    }


    public void addToBucket(ParameterType parameterType, String tag, String key, int version) {
        switch (parameterType) {
            case Spot:
                taggedBucket.get(tag).setSpotsnap(key, version);
                break;
            case Vol:
                taggedBucket.get(tag).setVolsnap(key, version);
                break;
            case YieldCurve:
                taggedBucket.get(tag).setYieldCurveSnap(key, version);
                break;
        }
    }

    public void freeAddress(String cacheId, int addressLoc) {
        requestSentToEngines.computeIfPresent(cacheId, (s, integer) -> integer -1);
        LOGGER.info("  %%%% After free address for {} , size is {} %%%% ", cacheId, requestSentToEngines.get(cacheId));
        if(requestSentToEngines.get(cacheId) == 0){
            requestSentToEngines.remove(cacheId);
            memoryMapManager.markFreeInBuffer(cacheId, addressLoc);
        }
    }

    public void processWatermark() {
        snapshotAllocationMessageMap.forEach((key, value) -> {
            if (!requestSentToEngines.containsKey(key) && value.isUpdate()) {
                String topic = this.engineRegMessageConsumer.getTopic(key.split(":")[0]);
                LOGGER.info("************ SENDING UPDATE MESSAGE NOW {} on topic {}, key{} ********** ", value, topic, key);
                snapshotAllocationMessageProducer.sendAllocationMessage(value, topic);
                value.setUpdate(false);
                requestSentToEngines.put(key.split(":")[0], value.getAddressUpdates().size());
            }
        });
    }

    public void closeBucket(String tag) {
        processPreviousSnapshot(Objects.requireNonNull(this.prevSnapshot.getIfPresent(tag)));
        CompositeSnapshotWindow compositeSnapshotWindow = taggedBucket.get(tag);
        Set<String> rootAddressesFree = Sets.newHashSet();
        compositeSnapshotWindow.getSpotKeys().forEach(s -> {
            memoryIndexRepository.getMemoryAddresses(s).forEach(address -> {
                LOGGER.info("Looking for start address {} ", address);
                Integer value = compositeSnapshotWindow.getSpotsnap().get(s);
                processEntry(tag, rootAddressesFree, address, value);
            });
        });

        compositeSnapshotWindow.getVolKeys().forEach(s -> {
            memoryIndexRepository.getMemoryAddresses(s).forEach(address -> {
                Integer value = compositeSnapshotWindow.getVolsnap().get(s);
                processEntry(tag, rootAddressesFree, address, value);
            });
        });

        compositeSnapshotWindow.getYieldCurveKeys().forEach(s -> {
            memoryIndexRepository.getMemoryAddresses(s).forEach(address -> {
                Integer value = compositeSnapshotWindow.getYieldCurveSnap().get(s);
                processEntry(tag, rootAddressesFree, address, value);
            });
        });
        this.snapshotAllocationMessageMapTemp.forEach((k, v) -> {
                    this.snapshotAllocationMessageMap.put(k, new SnapshotAllocationMessage(v));
                    v.getAddressUpdates().clear();
                }
        );
        rootAddressesFree.forEach(s -> this.memoryMapManager.markUsedInBuffer(s.split(":")[0],
                Integer.parseInt(s.split(":")[1])));
        compositeSnapshotWindow.closeWindow();
    }

    private void processEntry(String tag, Set<String> rootAddressesFree, String address, Integer value) {
        String cacheId = address.split(":")[0];
        int index = Integer.parseInt(address.split(":")[1]);
        LOGGER.info("CacheId {} , index {} , value{} ", cacheId, index, value);
        if(memoryMapManager.isFree(cacheId, index)){
            memoryMapManager.putInBuffer(cacheId, index, value);
            LOGGER.info("***** -----------------  DONE CacheId {} , index {} , value{} -------------------********** ", cacheId, index, value);
            rootAddressesFree.add(address);
            SnapshotAllocationMessage snapshotAllocationMessage =
                    this.snapshotAllocationMessageMapTemp.computeIfAbsent(address, s -> new SnapshotAllocationMessage());
            if(snapshotAllocationMessage.getMappedFile() == null){
                snapshotAllocationMessage.setCacheId(cacheId);
                snapshotAllocationMessage.setMappedFile(memoryMapManager.getMappedFileName(cacheId));
                snapshotAllocationMessage.setUpdate(true);
            }
            snapshotAllocationMessage.getAddressUpdates().add(index);
        }else {
            List<BackUpSnapshot> backupList = prevSnapshot.getIfPresent(tag);
            Objects.requireNonNull(backupList).add(new BackUpSnapshot(address, value));
        }
    }

    // if previous snpashot was not processes in last cycle - do so now
    // this is the default behaviour
    private void processPreviousSnapshot(List<BackUpSnapshot> backupList) {
        for (Iterator<BackUpSnapshot> iterator = backupList.iterator(); iterator.hasNext(); ) {
            BackUpSnapshot next = iterator.next();
            String address = next.loc;
            String cacheId = address.split(":")[0];
            int index = Integer.parseInt(address.split(":")[1]);
            if (memoryMapManager.isFree(cacheId, index)) {
                memoryMapManager.putInBuffer(cacheId, index, next.version);
                iterator.remove();
            }
        }
    }

}
