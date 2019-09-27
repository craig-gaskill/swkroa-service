package com.cagst.swkroa.service.user;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.cagst.swkroa.service.internal.util.LocalDateTimeUtil;
import org.springframework.jdbc.core.RowMapper;

/**
 * Maps a row in the ResultSet into a {@link User} object. Used to marshal / un-marshall a {@link User} object to
 * and from the database.
 *
 * @author Craig Gaskill
 */
/* package */final class UserMapper implements RowMapper<UserEntity> {
  private static final String USER_ID              = "user_id";
  private static final String PERSON_ID            = "person_id";
  private static final String USERNAME             = "username";
  private static final String PASSWORD             = "password";
  private static final String TEMPORARY_PWD_IND    = "temporary_pwd_ind";
  private static final String LOGIN_ATTEMPTS       = "signin_attempts";
  private static final String LAST_LOGIN_DATE_TIME = "last_signin_dt_tm";
  private static final String LAST_LOGIN_IP        = "last_signin_ip";
  private static final String LOCKED_DATE_TIME     = "account_locked_dt_tm";
  private static final String EXPIRED_DATE_TIME    = "account_expired_dt_tm";
  private static final String CHANGED_DATE_TIME    = "password_changed_dt_tm";
  private static final String USER_TYPE            = "user_type";

  // meta-data
  private static final String ACTIVE_IND   = "active_ind";
  private static final String UPDT_CNT     = "updt_cnt";

  @Override
  public UserEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
    return new UserEntity.Builder()
        .userId(rs.getLong(USER_ID))
        .personId(rs.getLong(PERSON_ID))
        .username(rs.getString(USERNAME))
        .password(rs.getString(PASSWORD))
        .temporary(rs.getBoolean(TEMPORARY_PWD_IND))
        .loginAttempts(rs.getLong(LOGIN_ATTEMPTS))
        .lastLoginDateTime(LocalDateTimeUtil.convertFromTimestamp(rs.getTimestamp(LAST_LOGIN_DATE_TIME)))
        .lastLoginIp(rs.getString(LAST_LOGIN_IP))
        .lockedDateTime(LocalDateTimeUtil.convertFromTimestamp(rs.getTimestamp(LOCKED_DATE_TIME)))
        .expiredDateTime(LocalDateTimeUtil.convertFromTimestamp(rs.getTimestamp(EXPIRED_DATE_TIME)))
        .changedDateTime(LocalDateTimeUtil.convertFromTimestamp(rs.getTimestamp(CHANGED_DATE_TIME)))
        .userType(UserType.valueOf(rs.getString(USER_TYPE)))
        .active(rs.getBoolean(ACTIVE_IND))
        .updateCount(rs.getLong(UPDT_CNT))
        .build();
  }
}
