package com.quantlogic.snapshot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WindowConfig {

    @Value(value = "${tag}")
    private String tags;

    public String getTags() {
        return tags;
    }
}
