package com.cagst.swkroa.service.security.token;

import reactor.core.publisher.Mono;

/**
 * Interface for retrieving / saving {@link Token} from / to persistent storage.
 *
 * @author Craig Gaskill
 */
public interface TokenRepository {
  /**
   * Checks to see if the specified token is valid (is active and not expired) for the specified user.
   *
   * @param userId
   *    The unique identifier of the User to see if the token is valid for.
   * @param token
   *    The token to check for validity on.
   *
   * @return {@code true} if the token is valid, {@code false} otherwise.
   */
  Mono<Boolean> isTokenValid(long userId, String token);

  /**
   * Inserts a new {@link Token} into persistent storage.
   *
   * @param token
   *    The {@link Token} to persist.
   */
  void insertToken(Token token);
}
