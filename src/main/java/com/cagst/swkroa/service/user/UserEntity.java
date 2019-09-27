package com.cagst.swkroa.service.user;

import java.time.LocalDateTime;

import com.cagst.swkroa.service.SwkroaBase;
import org.immutables.value.Value;
import org.springframework.lang.Nullable;

/**
 * Database representation of a User.
 *
 * @author Craig Gaskill
 */
@Value.Immutable(copy = false)
@Value.Style(visibility = Value.Style.ImplementationVisibility.PACKAGE, overshadowImplementation = true)
/* package */ interface UserEntity extends SwkroaBase {
  Long userId();

  Long personId();

  String username();

  @Value.Redacted
  String password();

  UserType userType();

  @Nullable
  LocalDateTime lastLoginDateTime();

  @Nullable
  String lastLoginIp();

  long loginAttempts();

  boolean temporary();

  @Nullable
  LocalDateTime lockedDateTime();

  @Nullable
  LocalDateTime expiredDateTime();

  @Nullable
  LocalDateTime changedDateTime();

  // static inner Builder class extends generated or yet-to-be generated Builder
  class Builder extends ImmutableUserEntity.Builder {}

}
