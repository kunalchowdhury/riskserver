package com.quantlogic.mmap;

import java.util.Queue;
import java.util.Set;

public interface MemoryMapManager {
    long getStartAddress(long memoryAddress);
    void set(long memoryAddress, int version);
    void markUsed(long memoryAddress);
    void markFree(long memoryAddress);
    boolean isFree(long memoryAddress);
    Set<Long> getUsedAddresses();
    Set<Long> getAddressSet();
    Queue<Long> getInitialAddresses();
    Long reserveMemory(String cacheId, int spotsCount, int volsCount, int yieldCurveCount);

}
