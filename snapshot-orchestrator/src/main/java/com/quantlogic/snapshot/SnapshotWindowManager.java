package com.quantlogic.snapshot;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.quantlogic.common.entity.SnapshotAllocationMessage;
import com.quantlogic.messaging.EngineRegMessageConsumer;
import com.quantlogic.messaging.SnapshotAllocationMessageProducer;
import com.quantlogic.mmap.MemoryMapManager;
import com.quantlogic.repository.MemoryIndexRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.BiConsumer;

@Component
public class SnapshotWindowManager {

    static class BackUpSnapshot {
        long address;
        int version;

        public BackUpSnapshot(long address, int version) {
            this.address = address;
            this.version = version;
        }
    }

    private final MemoryMapManager memoryMapManager;
    private final Cache<String, List<BackUpSnapshot>> prevSnapshot;
    private final EngineRegMessageConsumer engineRegMessageConsumer;
    private final SnapshotAllocationMessageProducer snapshotAllocationMessageProducer;
    private final Map<Long, SnapshotAllocationMessage> snapshotAllocationMessageMap;
    private final Set<Long> requestSentToEngines;

    public enum ParameterType {
        SPOT, VOLATILITY, YIELDCURVE
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
        this.snapshotAllocationMessageMap = new HashMap<>();
        this.requestSentToEngines = new HashSet<>();
        Arrays.stream(tags.split(",")).forEach(tag -> {
            taggedBucket.put(tag, new CompositeSnapshotWindow());
            this.prevSnapshot.put(tag, Lists.newArrayList());
        });

    }


    public void addToBucket(ParameterType parameterType, String tag, String key, int version) {
        switch (parameterType) {
            case SPOT:
                taggedBucket.get(tag).setSpotsnap(key, version);
                break;
            case VOLATILITY:
                taggedBucket.get(tag).setVolsnap(key, version);
                break;
            case YIELDCURVE:
                taggedBucket.get(tag).setYieldCurveSnap(key, version);
                break;
        }
    }

    public void freeAddress(long addressLoc) {
        requestSentToEngines.remove(addressLoc);
    }

    public void processWatermark() {
        snapshotAllocationMessageMap.forEach((key, value) -> {
            if(requestSentToEngines.contains(key)){
                String topic = this.engineRegMessageConsumer.getTopic(key);
                snapshotAllocationMessageProducer.sendAllocationMessage(value, topic);
                requestSentToEngines.add(key);
            }
        });
    }

    public void closeBucket(String tag) {
        processPreviousSnapshot(Objects.requireNonNull(this.prevSnapshot.getIfPresent(tag)));
        CompositeSnapshotWindow compositeSnapshotWindow = taggedBucket.get(tag);
        Set<Long> rootAddressesFree = Sets.newHashSet();
        compositeSnapshotWindow
                .getSpotsnap()
                .forEach(processEntry(compositeSnapshotWindow, rootAddressesFree, tag));

        compositeSnapshotWindow
                .getVolsnap()
                .forEach(processEntry(compositeSnapshotWindow, rootAddressesFree, tag));


        compositeSnapshotWindow
                .getYieldCurveSnap()
                .forEach(processEntry(compositeSnapshotWindow, rootAddressesFree, tag));

        rootAddressesFree.forEach(memoryMapManager::markUsed);
        compositeSnapshotWindow.closeWindow();
    }

    private BiConsumer<String, Integer> processEntry(CompositeSnapshotWindow compositeSnapshotWindow,
                                                     Set<Long> rootAddressesFree, String tag) {
        return (key, value) -> memoryIndexRepository.getMemoryAddresses(key).forEach(address -> {
            long startAddress = memoryMapManager.getStartAddress(address);
            if (compositeSnapshotWindow.getSpotKeys().contains(key)) {
                if (memoryMapManager.isFree(startAddress)) {
                    rootAddressesFree.add(startAddress);
                    memoryMapManager.set(address, value);
                    this.snapshotAllocationMessageMap.putIfAbsent(startAddress, new SnapshotAllocationMessage());
                    SnapshotAllocationMessage snapshotAllocationMessage = this.snapshotAllocationMessageMap.get(startAddress);
                    snapshotAllocationMessage.setStartMemAddress(startAddress);
                    snapshotAllocationMessage.setDone(false);
                } else {
                    List<BackUpSnapshot> backupList = prevSnapshot.getIfPresent(tag);
                    Objects.requireNonNull(backupList).add(new BackUpSnapshot(address, value));
                }
            }
        });
    }

    // if previous snpashot was not processes in last cycle - do so now
    // this is the default behaviour
    private void processPreviousSnapshot(List<BackUpSnapshot> backupList) {
        for (Iterator<BackUpSnapshot> iterator = backupList.iterator(); iterator.hasNext(); ) {
            BackUpSnapshot next = iterator.next();
            if (memoryMapManager.isFree(memoryMapManager.getStartAddress(next.address))) {
                memoryMapManager.set(next.address, next.version);
                iterator.remove();
            }
        }
    }

}
