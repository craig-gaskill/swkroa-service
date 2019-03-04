package com.cagst.swkroa.service.security.token;

import java.sql.Date;
import java.util.UUID;
import javax.sql.DataSource;

import com.cagst.swkroa.service.internal.jdbc.BaseRepositoryJdbc;
import com.cagst.swkroa.service.internal.jdbc.StatementLoader;
import com.cagst.swkroa.service.util.UuidAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

/**
 * JDBC implementation of the {@link TokenRepository} interface.
 *
 * @author Craig Gaskill
 */
@Repository
/* package */ class TokenRepositoryJdbc extends BaseRepositoryJdbc implements TokenRepository {
  private static final Logger LOGGER = LoggerFactory.getLogger(TokenRepositoryJdbc.class);

  private static final String IS_TOKEN_VALID = "IS_TOKEN_VALID";
  private static final String INSERT_TOKEN   = "INSERT_TOKEN";

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
  public boolean isTokenValid(long userId, String token) {
    LOGGER.debug("Calling isTokenValid for [{}, {}]", userId, token);

    StatementLoader stmtLoader = StatementLoader.getLoader(getClass(), getStatementDialect());

    UUID uuid = UUID.fromString(token);

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("token", UuidAdapter.convert(uuid));
    params.addValue("user_id", userId);

    Long cnt = getJdbcTemplate().queryForObject(stmtLoader.load(IS_TOKEN_VALID), params, Long.class);
    return (cnt != null && cnt != 0);
  }

  @Override
  public void insertToken(Token token) {
    LOGGER.debug("Calling insertToken for [{}]", token.token());

    StatementLoader stmtLoader = StatementLoader.getLoader(getClass(), getStatementDialect());

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("token", UuidAdapter.convert(token.token()));
    params.addValue("user_id", token.userId());
    params.addValue("expiry_dt_tm", Date.from(token.expiryDateTime().toInstant()));
    params.addValue("active_ind", token.active());

    getJdbcTemplate().update(stmtLoader.load(INSERT_TOKEN), params);
  }
}
