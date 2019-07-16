package com.cagst.swkroa.service.user;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Defines methods to retrieve and persist {@link User} objects.
 *
 * @author Craig Gaskill
 */
public interface UserService {
  /**
   * Will find the {@link User} for the specified username and increment the login attempts.
   *
   * @param username
   *    The username of the User to login.
   * @param password
   *    The raw text password used to authenticate with.
   * @param remoteAddress
   *    The remote address from where the call initiated.
   *
   * @return A {@link User} if found.
   */
  Mono<User> loginAttempt(String username, String password, String remoteAddress);

  /**
   * Updates the {@link User User's} record for a successful login.
   *
   * @param user
   *     The {@link User} to update.
   * @param ipAddress
   *     The ipAddress of where the user signed in from.
   *
   * @return A {@link User} that has been updated accordingly.
   *
   * @throws IllegalArgumentException
   *     if the <code>user</code> is <code>null</code>.
   */
  User loginSuccessful(User user, String ipAddress) throws IllegalArgumentException;

  /**
   * Updates the {@link User User's} record for a failed login.
   *
   * @param username
   *     The username used trying to sign in.
   * @param message
   *     A descriptive message of why the sign in failed.
   *
   * @throws IllegalArgumentException
   *     if the <code>username</code> is <code>null</code> or empty.
   */
  void loginFailure(String username, String message) throws IllegalArgumentException;

  /**
   * Locks the user's account by setting the locked date on the user's account.
   *
   * @param userId
   *   Uniquely identifies the user performing the request.
   * @param user
   *   The {@link User} to lock.
   *
   * @return A {@link User} that has been updated accordingly.
   */
  User lockAccount(long userId, User user);

  /**
   * Unlocks the user's account by clearing the locked date on the user's account.
   *
   * @param userId
   *   Uniquely identifies the user performing the request.
   * @param user
   *   The {@link User} to unlock.
   *
   * @return A {@link User} that has been updated accordingly.
   */
  User unlockAccount(long userId, User user);

  /**
   * Retrieves a {@link Flux} of {@link User}.
   *
   * @return A {@link Flux} of {@link User}.
   */
  Flux<User> getUsers();
}
