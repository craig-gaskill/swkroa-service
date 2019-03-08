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
    "tokenId",
    "tokenIdent",
    "userId",
    "expiryDtTm",
    "used",
    "active",
    "updateCount"
})
@Value
@Accessors(fluent = true)
@Builder(toBuilder = true)
@JsonDeserialize(builder = Token.TokenBuilder.class)
public class Token {
  @JsonProperty("tokenId")
  private Long tokenId;

  @JsonProperty("tokenIdent")
  private UUID tokenIdent;

  @JsonProperty("userId")
  private Long userId;

  @JsonProperty("expiryDtTm")
  private OffsetDateTime expiryDateTime;

  @JsonProperty("used")
  @Builder.Default
  private boolean used = false;

  @JsonProperty("active")
  @Builder.Default
  private boolean active = true;

  @JsonProperty("updateCount")
  @Builder.Default
  private long updateCount = 0L;
}
