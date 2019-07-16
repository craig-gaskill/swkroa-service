package com.cagst.swkroa.service.security;

import com.cagst.swkroa.service.user.User;

/**
 * Definition of a service that retrieves and persists {@link SecurityPolicy} objects.
 *
 * @author Craig Gaskill
 */
public interface SecurityService {
  /**
   * Retrieves the {@link SecurityPolicy} for the specified {@link User user}. Will look for any
   * security policy associated to the specific user, if not found will the retrieve the default
   * {@link SecurityPolicy}.
   *
   * @param user
   *     The {@link User} to retrieve the {@link SecurityPolicy} for
   *
   * @return The {@link SecurityPolicy} associated with the specified user.
   */
  SecurityPolicy getSecurityPolicy(User user);

  /**
   * Retrieves the default security policy.
   *
   * @return The default {@link SecurityPolicy}.
   */
  SecurityPolicy getDefaultSecurityPolicy();
}
