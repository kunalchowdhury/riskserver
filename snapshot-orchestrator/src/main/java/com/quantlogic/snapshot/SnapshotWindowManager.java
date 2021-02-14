package com.quantlogic.snapshot;

import com.quantlogic.mmap.MemoryMapManager;
import com.quantlogic.repository.MemoryIndexRepository;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.mapping;

@Component
public class SnapshotWindowManager {
    private final MemoryMapManager memoryMapManager;

    public enum ParameterType {
        SPOT, VOLATILITY, YIELDCURVE
    }

    private final Map<String, CompositeSnapshotWindow> taggedBucket;
    private final MemoryIndexRepository memoryIndexRepository;
    private final Map<String, Set<Long>> tagAddressesMap;

    public SnapshotWindowManager(@Autowired MemoryIndexRepository memoryIndexRepository,
                                 @Autowired WindowConfig windowConfig,
                                 @Autowired MemoryMapManager memoryMapManager) {
        this.taggedBucket = new HashMap<>();
        String tags = windowConfig.getTags();
        Arrays.stream(tags.split(",")).forEach(tag -> taggedBucket.put(tag, new CompositeSnapshotWindow()));
        this.memoryIndexRepository = memoryIndexRepository;
        this.memoryMapManager = memoryMapManager;
        this.tagAddressesMap = Arrays.stream(tags.split(","))
                .map(key -> memoryIndexRepository.getMemoryAddresses(key).stream()
                        .map(address -> Pair.of(key, memoryMapManager.getStartAddress(address)))
                ).flatMap(Stream::sorted)
                .collect(Collectors.groupingBy(Pair::getLeft, mapping(Pair::getRight, Collectors.toSet())));
    }


    public void addToBucket(ParameterType parameterType, String tag, String key, int version) {
        Optional<Long> elem = tagAddressesMap.get(key).stream().filter(k -> memoryMapManager.getUsedAddresses().contains(k)).findAny();
        switch (parameterType) {
            case SPOT:
                taggedBucket.get(tag).setSpotsnap(key, version, elem.isPresent());
                break;
            case VOLATILITY:
                taggedBucket.get(tag).setVolsnap(key, version, elem.isPresent());
                break;
            case YIELDCURVE:
                taggedBucket.get(tag).setYieldCurveSnap(key, version, elem.isPresent());
                break;
        }
    }

    public void reserveAddress(int address, boolean free) {
        if (free) {
            memoryMapManager.markFree(address);
        } else {
            memoryMapManager.markUsed(address);
        }
    }

    public void closeBucket(String tag) {
        CompositeSnapshotWindow compositeSnapshotWindow = taggedBucket.get(tag);
        compositeSnapshotWindow
                .getSpotsnap()
                .entrySet()
                .stream()
                .filter(e -> compositeSnapshotWindow.getSpotKeys().contains(e.getKey()))
                .forEach(e -> {
                    memoryIndexRepository.getMemoryAddresses(e.getKey()).forEach(memadd -> memoryMapManager.set(memadd, e.getValue()));
                });
        compositeSnapshotWindow
                .getVolsnap()
                .entrySet()
                .stream()
                .filter(e -> compositeSnapshotWindow.getVolKeys().contains(e.getKey()))
                .forEach(e -> {
                    memoryIndexRepository.getMemoryAddresses(e.getKey()).forEach(memadd -> memoryMapManager.set(memadd, e.getValue()));
                });

        compositeSnapshotWindow
                .getYieldCurveSnap()
                .entrySet()
                .stream()
                .filter(e -> compositeSnapshotWindow.getYieldCurveKeys().contains(e.getKey()))
                .forEach(e -> {
                    memoryIndexRepository.getMemoryAddresses(e.getKey()).forEach(memadd -> memoryMapManager.set(memadd, e.getValue()));
                });

        compositeSnapshotWindow.closeWindow();


    }

}
