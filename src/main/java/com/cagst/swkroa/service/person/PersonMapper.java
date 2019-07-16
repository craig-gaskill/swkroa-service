package com.cagst.swkroa.service.person;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;

/**
 * Used to marshal/unmarshal a {@link Person} to/from the database.
 *
 * @author Craig Gaskill
 */
public final class PersonMapper implements RowMapper<Person> {
  private static final String PERSON_ID       = "person_id";
  private static final String TITLE_CD        = "title_cd";
  private static final String NAME_LAST       = "name_last";
  private static final String NAME_LAST_KEY   = "name_last_key";
  private static final String NAME_FIRST      = "name_first";
  private static final String NAME_FIRST_KEY  = "name_first_key";
  private static final String NAME_MIDDLE     = "name_middle";
  private static final String LOCALE_LANGUAGE = "locale_language";
  private static final String LOCALE_COUNTRY  = "locale_country";
  private static final String TIME_ZONE       = "time_zone";

  // meta-data
  private static final String ACTIVE_IND = "active_ind";
  private static final String UPDT_CNT   = "updt_cnt";

  @Override
  public Person mapRow(final ResultSet rs, final int rowNum) throws SQLException {
    long titleCd    = rs.getLong(TITLE_CD);
    String language = rs.getString(LOCALE_LANGUAGE);
    String country  = rs.getString(LOCALE_COUNTRY);

    Locale locale = Locale.US;
    if (StringUtils.isNotEmpty(language)) {
      if (StringUtils.isNotEmpty(country)) {
        locale = new Locale(language, country);
      } else {
        locale = new Locale(language);
      }
    }

    return Person.builder()
        .personId(rs.getLong(PERSON_ID))
        .titleCd(titleCd > 0 ? titleCd : null)
        .firstName(rs.getString(NAME_FIRST))
        .middleName(rs.getString(NAME_MIDDLE))
        .lastName(rs.getString(NAME_LAST))
        .locale(locale)
        .active(rs.getBoolean(ACTIVE_IND))
        .updateCount(rs.getLong(UPDT_CNT))
        .build();
  }
}
