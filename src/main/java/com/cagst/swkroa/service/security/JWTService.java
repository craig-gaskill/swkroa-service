package com.cagst.swkroa.service.security;

import com.cagst.swkroa.service.user.User;

/**
 * Defines methods for generating an Access and a Refresh token.
 *
 * @author Craig Gaskill
 */
public interface JWTService {
  /**
   * Generates an access token for the specified User.
   *
   * @param user
   *     The {@link User} the Token is to be generated for.
   *
   * @return A String representation of a JWT for the specified User.
   */
  String generateAccessToken(User user);

  /**
   * Generates a refresh token for the specified User.
   *
   * @param user
   *     The {@link User} the Token is to be generated for.
   *
   * @return A String representation of a JWT for the specified User.
   */
  String generateRefreshToken(User user);

  /**
   * Will check the specified token to determine if it is valid or not.
   *
   * @param token
   *    The token to validate.
   *
   * @return {@code true} if the token is valid and has not expired.
   */
  boolean isTokenValid(String token);

  /**
   * Will return the UserId for the user associated with the specified token (if valid).
   *
   * @param token
   *    The token to validate and retrieve the UserId from.
   *
   * @return The unique identifier of the User the token is associated with.
   */
  long getUserId(String token);

  /**
   * Will return the list of roles for the user associated with the specified token (if valid).
   *
   * @param token
   *    The token to validate and retrieve the list of roles from.
   *
   * @return An array of roles of the User the token is associated with.
   */
  String[] getUserRoles(String token);
}
