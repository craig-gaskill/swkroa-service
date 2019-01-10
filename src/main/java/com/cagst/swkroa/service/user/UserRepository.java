package com.cagst.swkroa.service.user;

import java.util.Optional;

import com.cagst.swkroa.service.security.SecurityPolicy;

/**
 * Definition of a repository that retrieves and persists {@link User} objects.
 *
 * @author Craig Gaskill
 */
public interface UserRepository {
  /**
   * Retrieves a {@link User} based upon the specified username.
   *
   * @param username
   *   The {@link String} username that identifies the {@link User} to retrieve.
   *
   * @return An {@link Optional} that may contain the {@link User} associated to the specified username.
   */
  Optional<User> getUserByUsername(String username);

  /**
   * Updates the {@link User} account for a login attempt.
   *
   * @param user
   *   The {@link User} attempting to login.
   *
   * @return The {@link User} that has been updated accordingly.
   */
  User incrementLoginAttempts(User user);

  /**
   * Updates the {@link User} account for a successful login.
   *
   * @param user
   *     The {@link User} to update.
   * @param ipAddress
   *     The ipAddress where the user was when they successfully logged in.
   *
   * @return The {@link User} that has been updated accordingly.
   *
   * @throws IllegalArgumentException
   *     if <code>user</code> is null
   */
  User loginSuccessful(User user, String ipAddress) throws IllegalArgumentException;

  /**
   * Locks the user account as of NOW. Used primarily when the user has exceeded their sign-in
   * attempts.
   *
   * @param userId
   *   Uniquely identifies the user performing the request.
   * @param user
   *   The {@link User} who's account is to be locked.
   *
   * @return The {@link User} that has been locked and updated accordingly.
   */
  User lockUserAccount(long userId, User user);

  /**
   * Unlocks the user account as of NOW. Used by the system to automatically unlock a user account
   * when they have gone past the Account Locked Days according to the {@link SecurityPolicy}
   * defined for the user or by another user to manually unlock a user account.
   *
   * @param userId
   *   Uniquely identifies the user performing the request.
   * @param user
   *   The {@link User} who's account is to be unlocked.
   *
   * @return The {@link User} that has been successfully unlocked and updated accordingly.
   */
  User unlockUserAccount(long userId, User user);
}
