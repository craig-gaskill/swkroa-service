package com.cagst.swkroa.service.security;

import com.cagst.swkroa.service.SwkroaBase;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;
import org.springframework.lang.Nullable;

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
@JsonDeserialize(builder = SecurityPolicy.Builder.class)
@Value.Immutable(copy = false)
@Value.Style(visibility = Value.Style.ImplementationVisibility.PACKAGE, overshadowImplementation = true)
public interface SecurityPolicy extends SwkroaBase {
    public static final SecurityPolicy DEFAULT_SECURITY_POLICY = new SecurityPolicy.Builder()
        .name("Default")
        .build();

    @JsonProperty("securityPolicyId")
    @Nullable
    @Value.Auxiliary
    Long securityPolicyId();

    @JsonProperty("name")
    String name();

    @JsonProperty("maxAttempts")
    @Value.Default
    default int maxAttempts() {
        return 5;
    }

    @JsonProperty("timeoutInMinutes")
    @Value.Default
    default int timeoutInMinutes() {
        return 15;
    }

    @JsonProperty("expiryInDays")
    @Value.Default
    default int expiryInDays() {
        return 90;
    }

    @JsonProperty("lockedInMinutes")
    @Value.Default
    default int lockedInMinutes() {
        return 7 * 24 * 60;
    }

    // static inner Builder class extends generated or yet-to-be generated Builder
    class Builder extends ImmutableSecurityPolicy.Builder {}
}
