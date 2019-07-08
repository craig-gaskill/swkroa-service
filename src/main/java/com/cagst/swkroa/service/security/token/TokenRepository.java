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
   * @param tokenIdent
   *    The token to check for validity on.
   *
   * @return {@code true} if the token is valid, {@code false} otherwise.
   */
  Mono<Token> findTokenByIdent(long userId, String tokenIdent);

  /**
   * Inserts a new {@link Token} into persistent storage.
   *
   * @param userId
   *    The unique identifier of the User inserting the Token
   * @param token
   *    The {@link Token} to persist.
   *
   * @return The {@link Token} after it has been persisted.
   */
  Mono<Token> insertToken(long userId, Token token);

  /**
   * Updates the {@link Token} in persistent storage.
   *
   * @param userId
   *    The unique identifier of the User updating the Token
   * @param token
   *    The {@link Token} to update.
   *
   * @return The {@link Token} after it has been persisted.
   */
  Mono<Token> updateToken(long userId, Token token);
}
