package com.cagst.swkroa.service.user;

import java.time.LocalDateTime;

import com.cagst.swkroa.service.SwkroaBase;
import com.cagst.swkroa.service.person.Person;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.immutables.value.Value;
import org.springframework.lang.Nullable;

/**
 * Representation of a User.
 *
 * @author Craig Gaskill
 */
@JsonPropertyOrder({
    "userId",
    "person",
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
@Value.Immutable(copy = false)
@Value.Style(visibility = Value.Style.ImplementationVisibility.PACKAGE, overshadowImplementation = true)
public interface User extends SwkroaBase {
  @JsonProperty("userId")
  @Nullable
  @Value.Auxiliary
  Long userId();

  @JsonProperty("person")
  Person person();

  @JsonProperty("username")
  String username();

  @JsonProperty("userType")
  @Value.Default
  default UserType userType() {
    return UserType.STAFF;
  }

  @JsonProperty("lastLoginDateTime")
  @Nullable
  LocalDateTime lastLoginDateTime();

  @JsonProperty("lastLoginIp")
  @Nullable
  String lastLoginIp();

  @JsonProperty("temporary")
  @Value.Default
  default boolean temporary() {
    return true;
  }

  @JsonProperty("lockedDateTime")
  @Nullable
  LocalDateTime lockedDateTime();

  @JsonProperty("expireDateTime")
  @Nullable
  LocalDateTime expiredDateTime();

  @JsonProperty("changedDateTime")
  @Nullable
  LocalDateTime changedDateTime();

  // static inner Builder class extends generated or yet-to-be generated Builder
  class Builder extends ImmutableUser.Builder {}
}
