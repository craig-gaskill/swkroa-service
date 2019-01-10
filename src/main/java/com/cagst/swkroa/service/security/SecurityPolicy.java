package com.cagst.swkroa.service.security;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;

/**
 * Represents a Security Policy defined in the system.
 *
 * @author Craig Gaskill
 */
@JsonPropertyOrder({
    "securityPolicyId",
    "name",
    "maxAttempts",
    "timeoutInMinutes",
    "expiryInDays",
    "lockedInMinutes",
    "active",
    "updatedCount"
})
@Value
@Accessors(fluent = true)
@Builder(toBuilder = true)
@JsonDeserialize(builder = SecurityPolicy.SecurityPolicyBuilder.class)
public class SecurityPolicy implements Serializable {
    public static final SecurityPolicy DEFAULT_SECURITY_POLICY = SecurityPolicy.builder()
        .name("Default")
        .maxAttempts(5)
        .timeoutInMinutes(15)
        .expiryInDays(90)
        .lockedInMinutes(7 * 24 * 60)
        .active(true)
        .updateCount(0)
        .build();

    @JsonProperty("securityPolicyId")
    private long securityPolicyId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("maxAttempts")
    private int maxAttempts;

    @JsonProperty("timeoutInMinutes")
    private int timeoutInMinutes;

    @JsonProperty("expiryInDays")
    private int expiryInDays;

    @JsonProperty("lockedInMinutes")
    private int lockedInMinutes;

    @JsonProperty("active")
    private boolean active;

    @JsonProperty("updateCount")
    private long updateCount;
}
