package com.quantlogic.common.entity;

import java.util.Objects;

public class CacheKey {
    private int version;
    private String name;

    public CacheKey(int version, String name) {
        this.version = version;
        this.name = name;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CacheKey cacheKey = (CacheKey) o;
        return version == cacheKey.version && Objects.equals(name, cacheKey.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, name);
    }
}
