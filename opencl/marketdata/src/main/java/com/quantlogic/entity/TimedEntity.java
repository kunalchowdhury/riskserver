package com.quantlogic.entity;

public interface TimedEntity {
    long snaptime();
    int shardId();
}
