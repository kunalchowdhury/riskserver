package com.quantlogic.mmap;

import java.util.Set;

public interface MemoryMapManager {
    void set(long memoryAddress, int version);
    void putInBuffer(String cacheId, int index, int version);
    String getMappedFileName(String cacheId);
    void markUsedInBuffer(String cacheId, int index);
    void markFreeInBuffer(String cacheId, int index);
    boolean isFree(String cacheId, int idx);
    Long reserveMemory(String cacheId, int spotsCount, int volsCount, int yieldCurveCount);
    int getValue(long address);
    long getStartAddress(long memoryAddress);
    void markUsed(long memoryAddress);
    void markFree(long memoryAddress);
    boolean isFree(long memoryAddress);
    Set<Long> getUsedAddresses();
    Set<Long> getAddressSet();

}
