package com.quantlogic.entity;

public abstract class Volatility implements Entity{
    private final int id;
    private final String name;
    private final int version;
    private final long insertTime;

    public Volatility(int id, String name, int version, long insertTime) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.insertTime = insertTime;
    }

    @Override
    public Type getType() {
        return Type.VOLATILITY_SURFACE;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public long insertTime() {
        return insertTime;
    }
}
