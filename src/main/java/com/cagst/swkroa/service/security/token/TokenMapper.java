package com.cagst.swkroa.service.security.token;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;

import com.cagst.swkroa.service.util.UuidAdapter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

/**
 * Maps a row in the result set into a {@link Token} object. Used to un-marshal a Token from
 * the database.
 *
 * @author Craig Gaskill
 */
/* package */ class TokenMapper implements RowMapper<Token> {
  private static final String TOKEN_IDENT  = "token_ident";
  private static final String USER_ID      = "user_id";
  private static final String EXPIRY_DT_TM = "expiry_dt_tm";
  private static final String USED_IND     = "used_ind";
  private static final String ACTIVE_IND   = "active_ind";

  @Override
  public Token mapRow(ResultSet rs, int rowNum) throws SQLException {
    return Token.builder()
        .token(UuidAdapter.convert(rs.getString(TOKEN_IDENT).getBytes()))
        .userId(rs.getLong(USER_ID))
        .expiryDateTime(rs.getTimestamp(EXPIRY_DT_TM).toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime())
        .used(rs.getBoolean(USED_IND))
        .active(rs.getBoolean(ACTIVE_IND))
        .build();
  }

  /**
   * Will marshal a {@link Token} into a {@link MapSqlParameterSource} for inserting into the database.
   *
   * @param token
   *    The {@link Token} to map into an insert statement.
   *
   * @return A {@link MapSqlParameterSource} that can be used in a {@code jdbcTemplate.update} statement.
   */
  static MapSqlParameterSource mapForInsert(Token token) {
    return mapCommonParameters(token);
  }

  /**
   * Will marshal a {@link Token} into a {@link MapSqlParameterSource} for updating in the database.
   *
   * @param token
   *    The {@link Token} to map into an insert statement.
   *
   * @return A {@link MapSqlParameterSource} that can be used in a {@code jdbcTemplate.update} statement.
   */
  static MapSqlParameterSource mapForUpdate(Token token) {
    return mapCommonParameters(token);
  }

  private static MapSqlParameterSource mapCommonParameters(Token token) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("token", UuidAdapter.convert(token.token()));
    params.addValue("user_id", token.userId());
    params.addValue("expiry_dt_tm", Date.from(token.expiryDateTime().toInstant()));
    params.addValue("used_ind", token.used());
    params.addValue("active_ind", token.active());

    return params;
  }
}
