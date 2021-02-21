package com.quantlogic.repository;

import java.util.List;

public interface MemoryIndexRepository {
    void mapMemoryAddress(String paramKey, String memoryAddress);
    void unmapMemoryAddress(String paramKey);
    List<String> getMemoryAddresses(String paramKey);

}
