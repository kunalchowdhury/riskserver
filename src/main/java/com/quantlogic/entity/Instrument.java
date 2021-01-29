package com.quantlogic.entity;

public abstract class Instrument implements Entity{
    private final int id;
    private final String name;

    public Instrument(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public Type getType() {
        return Type.INSTRUMENT;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }
}
