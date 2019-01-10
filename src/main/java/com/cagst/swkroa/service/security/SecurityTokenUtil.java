package com.cagst.swkroa.service.security;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * A utility class to retrieve the security token.
 *
 * @author Craig Gaskill
 */
public abstract class SecurityTokenUtil {
  private static final Logger LOGGER = LoggerFactory.getLogger(SecurityTokenUtil.class);

  /**
   * Will attempt to retrieve the unique identifier of the User that is currently logged in
   * from the spring security context.
   *
   * @return The unique identifier of the User that is currently logged in, <code>null</code>
   * if the security context has already been cleared by spring security.
   */
  @Nullable
  public static Long getUserId() {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    if (securityContext == null) {
      LOGGER.warn("The SecurityContextHolder SecurityContext is null.");
      return null;
    }

    Authentication auth = securityContext.getAuthentication();
    if (auth == null) {
      LOGGER.warn("The SecurityContextHolder Authentication is null.");
      return null;
    }

    if (!(auth.getPrincipal() instanceof Long)) {
      return null;
    }

    return ((Long)auth.getPrincipal());
  }
}
