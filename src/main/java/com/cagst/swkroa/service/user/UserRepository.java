package com.cagst.swkroa.service.user;

import java.util.List;
import java.util.Optional;

import com.cagst.swkroa.service.security.SecurityPolicy;
import reactor.core.publisher.Flux;

/**
 * Definition of a repository that retrieves and persists {@link UserEntity} objects.
 *
 * @author Craig Gaskill
 */
public interface UserRepository {
  /**
   * Retrieves a {@link UserEntity} based upon the specified username.
   *
   * @param username
   *   The {@link String} username that identifies the {@link UserEntity} to retrieve.
   *
   * @return An {@link Optional} that may contain the {@link UserEntity} associated to the specified username.
   */
  Optional<UserEntity> getUserByUsername(String username);

  /**
   * Updates the {@link UserEntity} account for a login attempt.
   *
   * @param user
   *   The {@link UserEntity} attempting to login.
   *
   * @return The {@link UserEntity} that has been updated accordingly.
   */
  UserEntity incrementLoginAttempts(UserEntity user);

  /**
   * Updates the {@link UserEntity} account for a successful login.
   *
   * @param user
   *     The {@link UserEntity} to update.
   * @param ipAddress
   *     The ipAddress where the user was when they successfully logged in.
   *
   * @return The {@link UserEntity} that has been updated accordingly.
   *
   * @throws IllegalArgumentException
   *     if <code>UserEntity</code> is null
   */
  UserEntity loginSuccessful(UserEntity user, String ipAddress) throws IllegalArgumentException;

  /**
   * Locks the user account as of NOW. Used primarily when the user has exceeded their sign-in
   * attempts.
   *
   * @param userId
   *   Uniquely identifies the user performing the request.
   * @param user
   *   The {@link UserEntity} who's account is to be locked.
   *
   * @return The {@link UserEntity} that has been locked and updated accordingly.
   */
  UserEntity lockUserAccount(long userId, UserEntity user);

  /**
   * Unlocks the user account as of NOW. Used by the system to automatically unlock a user account
   * when they have gone past the Account Locked Days according to the {@link SecurityPolicy}
   * defined for the user or by another user to manually unlock a user account.
   *
   * @param userId
   *   Uniquely identifies the user performing the request.
   * @param user
   *   The {@link UserEntity} who's account is to be unlocked.
   *
   * @return The {@link UserEntity} that has been successfully unlocked and updated accordingly.
   */
  UserEntity unlockUserAccount(long userId, UserEntity user);

  /**
   * Retrieves a {@link List} of all {@link UserEntity Users} that are in the system.
   *
   * @return A {@link List} of {@link UserEntity Users} defined in the system.
   */
  Flux<UserEntity> getUsers();

}
