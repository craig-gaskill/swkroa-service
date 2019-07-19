package com.cagst.swkroa.service.user;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
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
  Mono<User> loginAttempt(@NonNull String username, @NonNull String password, @Nullable String remoteAddress);

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
  Mono<User> lockAccount(long userId, Mono<User> user);

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
  Mono<User> unlockAccount(long userId, Mono<User> user);

  /**
   * Retrieves a {@link Flux} of {@link User}.
   *
   * @return A {@link Flux} of {@link User}.
   */
  Flux<User> getUsers();
}
