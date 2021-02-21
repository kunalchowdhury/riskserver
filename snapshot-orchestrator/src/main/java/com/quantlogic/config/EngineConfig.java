package com.quantlogic.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EngineConfig {

    @Value(value = "${engine.count}")
    private int engineCount;

    public int getEngineCount() {
        return engineCount;
    }
}
