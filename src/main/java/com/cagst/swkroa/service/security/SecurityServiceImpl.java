package com.cagst.swkroa.service.security;

import com.cagst.swkroa.service.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * An implementation of the {@link SecurityService} interface.
 *
 * @author Craig Gaskill
 */
@Service
public class SecurityServiceImpl implements SecurityService {
  private int maxAttempts = 5;
  private int timeoutInMinutes = 15;
  private int expiryInDays = 90;
  private int lockedInDays = 7;

  private SecurityPolicy defaultSecurityPolicy;

  public int getMaximumAttempts() {
    return maxAttempts;
  }

  @Value("${security.max.attempts:5}")
  public void setMaximumAttempts(int maxAttempts) {
    this.maxAttempts = maxAttempts;
  }

  public int getTimeoutInMinutes() {
    return timeoutInMinutes;
  }

  @Value("${security.timeout.minutes:15}")
  public void setTimeoutInMinutes(int timeoutInMinutes) {
    this.timeoutInMinutes = timeoutInMinutes;
  }

  public int getExpiryInDays() {
    return expiryInDays;
  }

  @Value("${security.expiry.days:90}")
  public void setExpiryInDays(final int expiryInDays) {
    this.expiryInDays = expiryInDays;
  }

  public int getLockedDays() {
    return lockedInDays;
  }

  @Value("${security.locked.days:7}")
  public void setLockedDays(final int lockedInDays) {
    this.lockedInDays = lockedInDays;
  }

  @Override
  public SecurityPolicy getSecurityPolicy(User user) {
    return getDefaultSecurityPolicy();
  }

  private SecurityPolicy getDefaultSecurityPolicy() {
    if (null == defaultSecurityPolicy) {
      defaultSecurityPolicy = SecurityPolicy.builder()
          .name("Default")
          .maxAttempts(getMaximumAttempts())
          .timeoutInMinutes(getTimeoutInMinutes())
          .expiryInDays(getExpiryInDays())
          .lockedInMinutes(getLockedDays() * 24 * 60)
          .build();
    }

    return defaultSecurityPolicy;
  }
}
