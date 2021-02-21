package com.quantlogic.engine;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EngineConfig {

    @Value(value = "${engine.id}")
    private int engineId;

    @Value(value = "${cox.rubenstein.engine.steps.count}")
    private int crrSteps;

    @Value(value = "${marker.topic}")
    private String markerTopic;

    public int getEngineId() {
        return engineId;
    }

    public int getCrrSteps() {
        return crrSteps;
    }

    public String getMarkerTopic() {
        return markerTopic;
    }
}
