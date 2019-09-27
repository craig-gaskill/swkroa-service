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
  private static final String TOKEN_ID     = "token_id";
  private static final String TOKEN_IDENT  = "token_ident";
  private static final String USER_ID      = "user_id";
  private static final String EXPIRY_DT_TM = "expiry_dt_tm";
  private static final String USED_IND     = "used_ind";

  // meta-data
  private static final String ACTIVE_IND   = "active_ind";
  private static final String CREATE_ID    = "create_id";
  private static final String UPDATE_ID    = "updt_id";
  private static final String UPDATE_CNT   = "updt_cnt";

  @Override
  public Token mapRow(ResultSet rs, int rowNum) throws SQLException {
    return new Token.Builder()
        .tokenId(rs.getLong(TOKEN_ID))
        .tokenIdent(UuidAdapter.convert(rs.getString(TOKEN_IDENT).getBytes()))
        .userId(rs.getLong(USER_ID))
        .expiryDateTime(rs.getTimestamp(EXPIRY_DT_TM).toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime())
        .used(rs.getBoolean(USED_IND))
        .active(rs.getBoolean(ACTIVE_IND))
        .updateCount(rs.getLong(UPDATE_CNT))
        .build();
  }

  /**
   * Will marshal a {@link Token} into a {@link MapSqlParameterSource} for inserting into the database.
   *
   * @param userId
   *    The unique identifier of the User inserting the Token
   * @param token
   *    The {@link Token} to map into an insert statement.
   *
   * @return A {@link MapSqlParameterSource} that can be used in a {@code jdbcTemplate.update} statement.
   */
  static MapSqlParameterSource mapForInsert(long userId, Token token) {
    MapSqlParameterSource params = mapCommonParameters(userId, token);

    params.addValue(TOKEN_IDENT, UuidAdapter.convert(token.tokenIdent()));
    params.addValue(CREATE_ID, userId);

    return params;
  }

  /**
   * Will marshal a {@link Token} into a {@link MapSqlParameterSource} for updating in the database.
   *
   * @param userId
   *    The unique identifier of the User updating the Token
   * @param token
   *    The {@link Token} to map into an insert statement.
   *
   * @return A {@link MapSqlParameterSource} that can be used in a {@code jdbcTemplate.update} statement.
   */
  static MapSqlParameterSource mapForUpdate(long userId, Token token) {
    MapSqlParameterSource params = mapCommonParameters(userId, token);

    params.addValue(TOKEN_ID, token.tokenId());
    params.addValue(UPDATE_CNT, token.updateCount());

    return params;
  }

  private static MapSqlParameterSource mapCommonParameters(long userId, Token token) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue(USER_ID, token.userId());
    params.addValue(EXPIRY_DT_TM, Date.from(token.expiryDateTime().toInstant()));
    params.addValue(USED_IND, token.used());
    params.addValue(ACTIVE_IND, token.active());
    params.addValue(UPDATE_ID, userId);

    return params;
  }
}
