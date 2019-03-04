package com.cagst.swkroa.service.security.token;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;

/**
 * Represents a Token (refresh token).
 *
 * @author Craig Gaskill
 */
@JsonPropertyOrder({
    "token",
    "userId",
    "expiryDtTm",
    "active"
})
@Value
@Accessors(fluent = true)
@Builder(toBuilder = true)
@JsonDeserialize(builder = Token.TokenBuilder.class)
public class Token {
  @JsonProperty("token")
  private UUID token;

  @JsonProperty("userId")
  private Long userId;

  private OffsetDateTime expiryDateTime;

  @JsonProperty("active")
  @Builder.Default
  private boolean active = true;
}
