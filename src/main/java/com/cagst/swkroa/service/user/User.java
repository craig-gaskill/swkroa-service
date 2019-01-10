package com.cagst.swkroa.service.user;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;

/**
 * Representation of a User.
 *
 * @author Craig Gaskill
 */
@JsonPropertyOrder({
    "userId",
    "username",
    "userType",
    "lastLoginDateTime",
    "lastLoginIp",
    "temporary",
    "lockedDateTime",
    "expireDateTime",
    "changedDateTime",
    "active",
    "updateCount"
})
@Value
@Accessors(fluent = true)
@Builder(toBuilder = true)
public class User {
  @JsonProperty("userId")
  private Long userId;

  @JsonProperty("username")
  private String username;

  @JsonIgnore
  private String password;

  @JsonProperty("userType")
  private UserType userType;

  @JsonProperty("lastLoginDateTime")
  private LocalDateTime lastLoginDateTime;

  @JsonProperty("lastLoginIp")
  private String lastLoginIp;

  @JsonIgnore
  private long loginAttempts;

  @JsonProperty("temporary")
  private boolean temporary;

  @JsonProperty("lockedDateTime")
  private LocalDateTime lockedDateTime;

  @JsonProperty("expireDateTime")
  private LocalDateTime expiredDateTime;

  @JsonProperty("changedDateTime")
  private LocalDateTime changedDateTime;

  @JsonProperty("active")
  private boolean active;

  @JsonProperty("updateCount")
  private long updateCount;
}
