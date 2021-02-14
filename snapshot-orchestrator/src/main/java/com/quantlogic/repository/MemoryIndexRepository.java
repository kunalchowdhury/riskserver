package com.quantlogic.repository;

import java.util.Collection;

public interface MemoryIndexRepository {
    void mapMemoryAddress(String paramKey, String memoryAddress);
    void unmapMemoryAddress(String paramKey);
    Collection<Long> getMemoryAddresses(String paramKey);

}
