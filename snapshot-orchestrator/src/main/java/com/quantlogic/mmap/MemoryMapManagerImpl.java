package com.quantlogic.mmap;

import com.google.common.collect.Queues;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

@Component
public class MemoryMapManagerImpl implements MemoryMapManager{

    private final Set<Long> addressSet;

    public MemoryMapManagerImpl() {
        this.addressSet = new HashSet<>();
    }

    @Override
    public long getStartAddress(long memoryAddress) {
        return 0;
    }

    @Override
    public void set(long memoryAddress, int version) {

    }

    @Override
    public void markUsed(long memoryAddress) {

    }

    @Override
    public void markFree(long memoryAddress) {

    }

    @Override
    public boolean isFree(long memoryAddress) {
        return false;
    }

    @Override
    public Set<Long> getUsedAddresses() {
        return Collections.emptySet();
    }

    public Set<Long> getAddressSet() {
        return addressSet;
    }

    @Override
    public Queue<Long> getInitialAddresses() {
        return Queues.newArrayDeque();
    }

    @Override
    public Long reserveMemory(String cacheId, int spotsCount, int volsCount, int yieldCurveCount) {
        return null;
    }
}
