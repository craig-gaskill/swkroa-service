package com.cagst.swkroa.service.user;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;

/**
 * Database representation of a User.
 *
 * @author Craig Gaskill
 */
@Value
@Accessors(fluent = true)
@Builder(toBuilder = true)
public class UserEntity {
  private Long userId;
  private Long personId;
  private String username;
  private String password;
  private UserType userType;
  private LocalDateTime lastLoginDateTime;
  private String lastLoginIp;
  private long loginAttempts;
  private boolean temporary;
  private LocalDateTime lockedDateTime;
  private LocalDateTime expiredDateTime;
  private LocalDateTime changedDateTime;
  private boolean active;
  private long updateCount;
}
