package com.cagst.swkroa.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.immutables.value.Value;

public interface SwkroaBase {
    @JsonProperty("active")
    @Value.Default
    default boolean active() {
        return true;
    }

    @JsonProperty("updateCount")
    @Value.Default
    default long updateCount() {
        return 0L;
    }
}
