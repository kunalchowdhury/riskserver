package com.quantlogic.mmap;

import java.util.Set;

public interface MemoryMapManager {
    long getStartAddress(long memoryAddress);
    void set(long memoryAddress, int version);
    void putInBuffer(String cacheId, int index, int version);
    String getMappedFileName(String cacheId);
    void markUsed(long memoryAddress);
    void markUsedInBuffer(String cacheId, int index);
    void markFreeInBuffer(String cacheId, int index);
    void markFree(long memoryAddress);
    boolean isFree(long memoryAddress);
    boolean isFree(String cacheId, int idx);
    Set<Long> getUsedAddresses();
    Set<Long> getAddressSet();
    Long reserveMemory(String cacheId, int spotsCount, int volsCount, int yieldCurveCount);
    int getValue(long address);
}
