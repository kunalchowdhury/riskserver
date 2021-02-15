package com.quantlogic.messaging;

public interface WaterMarkEmittingStrategy {
    boolean shouldEmit();
    WaterMarkEmittingStrategy ALWAYS_TRUE = () -> true;
}
