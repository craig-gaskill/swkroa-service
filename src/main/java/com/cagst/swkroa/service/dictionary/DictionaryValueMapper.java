package com.cagst.swkroa.service.dictionary;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

/**
 * Maps a row in the resultset into a {@link DictionaryValue} object. Used to unmarshall a DictionaryValue
 * from the database.
 *
 * @author Craig Gaskill
 */
/* package */class DictionaryValueMapper implements RowMapper<DictionaryValue> {
  private static final String DICTIONARY_ID       = "codeset_id";
  private static final String DICTIONARY_VALUE_ID = "codevalue_id";
  private static final String DISPLAY             = "codevalue_display";
  private static final String MEANING             = "codevalue_meaning";

  // meta-data
  private static final String ACTIVE_IND          = "active_ind";
  private static final String CREATE_ID           = "create_id";
  private static final String UPDATE_ID           = "updt_id";
  private static final String UPDATE_CNT          = "updt_cnt";

  @Override
  public DictionaryValue mapRow(ResultSet rs, int rowNum) throws SQLException {
    return DictionaryValue.builder()
        .dictionaryValueId(rs.getLong(DICTIONARY_VALUE_ID))
        .display(rs.getString(DISPLAY))
        .meaning(rs.getString(MEANING))
        .active(rs.getBoolean(ACTIVE_IND))
        .updateCount(rs.getLong(UPDATE_CNT))
        .build();
  }

  /**
   * Will marshal a {@link DictionaryValue} into a {@link MapSqlParameterSource} for inserting into the database.
   *
   * @param dictionaryValue
   *     The {@link DictionaryValue} to map into an insert statement.
   * @param userId
   *     The unique identifier of the user that performed the changes.
   *
   * @return A {@link MapSqlParameterSource} that can be used in a {@code jdbcTemplate.update} statement.
   */
  static MapSqlParameterSource mapForInsert(long userId, long dictionaryId, DictionaryValue dictionaryValue) {
    MapSqlParameterSource params = mapCommonParameters(userId, dictionaryValue);
    params.addValue(DICTIONARY_ID, dictionaryId);
    params.addValue(CREATE_ID, userId);

    return params;
  }

  /**
   * Will marshal a {@link DictionaryValue} into a {@link MapSqlParameterSource} for updating in the database.
   *
   * @param dictionaryValue
   *     The {@link DictionaryValue} to map into an update statement.
   * @param userId
   *     The unique identifier of the user that performed the changes.
   *
   * @return A {@link MapSqlParameterSource} that can be used in a {@code jdbcTemplate.update} statement.
   */
  static MapSqlParameterSource mapForUpdate(long userId, DictionaryValue dictionaryValue) {
    MapSqlParameterSource params = mapCommonParameters(userId, dictionaryValue);
    params.addValue(DICTIONARY_VALUE_ID, dictionaryValue.dictionaryValueId());
    params.addValue(UPDATE_CNT, dictionaryValue.updateCount());

    return params;
  }

  private static MapSqlParameterSource mapCommonParameters(long userId, DictionaryValue dictionaryValue) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue(DISPLAY, dictionaryValue.display());
    params.addValue(MEANING, dictionaryValue.meaning());
    params.addValue(ACTIVE_IND, dictionaryValue.active());
    params.addValue(UPDATE_ID, userId);

    return params;
  }
}
