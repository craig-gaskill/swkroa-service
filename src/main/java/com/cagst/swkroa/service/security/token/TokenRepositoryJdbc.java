package com.cagst.swkroa.service.security.token;

import java.util.List;
import java.util.UUID;
import javax.sql.DataSource;

import com.cagst.common.jdbc.BaseRepositoryJdbc;
import com.cagst.common.jdbc.StatementLoader;
import com.cagst.swkroa.service.util.UuidAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * JDBC implementation of the {@link TokenRepository} interface.
 *
 * @author Craig Gaskill
 */
@Repository
/* package */ class TokenRepositoryJdbc extends BaseRepositoryJdbc implements TokenRepository {
  private static final Logger LOGGER = LoggerFactory.getLogger(TokenRepositoryJdbc.class);

  private static final String FIND_TOKEN_BY_IDENT = "FIND_TOKEN_BY_IDENT";

  private static final String INSERT_TOKEN = "INSERT_TOKEN";
  private static final String UPDATE_TOKEN = "UPDATE_TOKEN";

  private final TokenMapper TOKEN_MAPPER = new TokenMapper();

  /**
   * Primary Constructor used to create an instance of the <i>TokenRepositoryJdbc</i> class.
   *
   * @param dataSource
   *     The {@link DataSource} used to retrieve / persist data objects.
   */
  protected TokenRepositoryJdbc(DataSource dataSource) {
    super(dataSource);
  }

  @Override
  public Mono<Token> findTokenByIdent(long userId, String tokenIdent) {
    LOGGER.debug("Calling findTokenByIdent for [{}, {}]", userId, tokenIdent);

    StatementLoader stmtLoader = StatementLoader.getLoader(getClass(), getStatementDialect());

    UUID uuid = UUID.fromString(tokenIdent);

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("token_ident", UuidAdapter.convert(uuid));
    params.addValue("user_id", userId);

    List<Token> tokens = getJdbcTemplate().query(stmtLoader.load(FIND_TOKEN_BY_IDENT), params, TOKEN_MAPPER);
    if (tokens != null && tokens.size() == 1) {
      return Mono.just(tokens.get(0));
    } else {
      return Mono.empty();
    }
  }

  @Override
  public Mono<Token> insertToken(long userId, Token token) {
    LOGGER.debug("Calling insertToken for [{}]", token.tokenIdent());

    StatementLoader stmtLoader = StatementLoader.getLoader(getClass(), getStatementDialect());
    KeyHolder keyHolder = new GeneratedKeyHolder();

    int cnt = getJdbcTemplate().update(
        stmtLoader.load(INSERT_TOKEN),
        TokenMapper.mapForInsert(userId, token),
        keyHolder);

    if (cnt != 1) {
      throw new IncorrectResultSizeDataAccessException(1, cnt);
    }

    return Mono.just(new Token.Builder()
        .from(token)
        .tokenId(keyHolder.getKey().longValue())
        .build());
  }

  @Override
  public Mono<Token> updateToken(long userId, Token token) {
    LOGGER.debug("Calling updateToken for [{}]", token.tokenIdent());

    StatementLoader stmtLoader = StatementLoader.getLoader(getClass(), getStatementDialect());

    int cnt = getJdbcTemplate().update(
        stmtLoader.load(UPDATE_TOKEN),
        TokenMapper.mapForUpdate(userId, token));

    if (cnt == 1) {
      return Mono.just(new Token.Builder()
          .from(token)
          .updateCount(token.updateCount() + 1)
          .build());
    } else if (cnt == 0) {
      throw new OptimisticLockingFailureException("invalid update count of [" + cnt
          + "] possible update count mismatch");
    } else {
      throw new IncorrectResultSizeDataAccessException(1, cnt);
    }
  }
}
