package com.quantlogic.common.entity;

public class CacheKey {
    private int version;
    private long snapshotTime;
    private String name;

    public CacheKey(int version, long snapshotTime, String name) {
        this.version = version;
        this.snapshotTime = snapshotTime;
        this.name = name;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public long getSnapshotTime() {
        return snapshotTime;
    }

    public void setSnapshotTime(long snapshotTime) {
        this.snapshotTime = snapshotTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
