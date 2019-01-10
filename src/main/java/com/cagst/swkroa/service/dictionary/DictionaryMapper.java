package com.cagst.swkroa.service.dictionary;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

/**
 * Maps a row in the result set into a {@link Dictionary} object. Used to un-marshall a Dictionary from the
 * database.
 *
 * @author Craig Gaskill
 */
/* package */ class DictionaryMapper implements RowMapper<Dictionary> {
  private static final String DICTIONARY_ID = "codeset_id";
  private static final String DISPLAY       = "codeset_display";
  private static final String MEANING       = "codeset_meaning";
  private static final String ACTIVE_IND    = "active_ind";
  private static final String UPDATE_CNT    = "updt_cnt";

  @Override
  public Dictionary mapRow(final ResultSet rs, final int rowNum) throws SQLException {
    return Dictionary.builder()
        .dictionaryId(rs.getLong(DICTIONARY_ID))
        .display(rs.getString(DISPLAY))
        .meaning(rs.getString(MEANING))
        .active(rs.getBoolean(ACTIVE_IND))
        .updateCount(rs.getInt(UPDATE_CNT))
        .build();
  }
}
