package com.cagst.swkroa.service.security.token;

import java.util.List;
import java.util.UUID;
import javax.sql.DataSource;

import com.cagst.swkroa.service.internal.jdbc.BaseRepositoryJdbc;
import com.cagst.swkroa.service.internal.jdbc.StatementLoader;
import com.cagst.swkroa.service.util.UuidAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
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

  private static final String FIND_TOKEN   = "FIND_TOKEN";
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
  public Mono<Token> findToken(long userId, String token) {
    LOGGER.debug("Calling findToken for [{}, {}]", userId, token);

    StatementLoader stmtLoader = StatementLoader.getLoader(getClass(), getStatementDialect());

    UUID uuid = UUID.fromString(token);

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("token", UuidAdapter.convert(uuid));
    params.addValue("user_id", userId);

    List<Token> tokens = getJdbcTemplate().query(stmtLoader.load(FIND_TOKEN), params, TOKEN_MAPPER);
    if (tokens != null && tokens.size() == 1) {
      return Mono.just(tokens.get(0));
    } else {
      return Mono.empty();
    }
  }

  @Override
  public void insertToken(Token token) {
    LOGGER.debug("Calling insertToken for [{}]", token.token());

    StatementLoader stmtLoader = StatementLoader.getLoader(getClass(), getStatementDialect());
    getJdbcTemplate().update(stmtLoader.load(INSERT_TOKEN), TokenMapper.mapForInsert(token));
  }

  @Override
  public void updateToken(Token token) {
    LOGGER.debug("Calling updateToken for [{}]", token.token());

    StatementLoader stmtLoader = StatementLoader.getLoader(getClass(), getStatementDialect());
    getJdbcTemplate().update(stmtLoader.load(INSERT_TOKEN), TokenMapper.mapForUpdate(token));
  }
}
