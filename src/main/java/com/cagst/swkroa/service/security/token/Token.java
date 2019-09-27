package com.cagst.swkroa.service.security.token;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.cagst.swkroa.service.SwkroaBase;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;
import org.springframework.lang.Nullable;

/**
 * Represents a Token (refresh token).
 *
 * @author Craig Gaskill
 */
@JsonPropertyOrder({
    "tokenId",
    "tokenIdent",
    "userId",
    "expiryDtTm",
    "used",
    "active",
    "updateCount"
})
@JsonDeserialize(builder = Token.Builder.class)
@Value.Immutable(copy = false)
@Value.Style(visibility = Value.Style.ImplementationVisibility.PACKAGE, overshadowImplementation = true)
public interface Token extends SwkroaBase {
  @Nullable
  @Value.Auxiliary
  @JsonProperty("tokenId")
  Long tokenId();

  @JsonProperty("tokenIdent")
  UUID tokenIdent();

  @JsonProperty("userId")
  long userId();

  @JsonProperty("expiryDtTm")
  OffsetDateTime expiryDateTime();

  @JsonProperty("used")
  @Value.Default
  default boolean used() {
    return false;
  }

  // static inner Builder class extends generated or yet-to-be generated Builder
  class Builder extends ImmutableToken.Builder {}
}
