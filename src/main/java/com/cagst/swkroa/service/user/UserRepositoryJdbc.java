package com.cagst.swkroa.service.user;

import java.time.LocalDateTime;
import java.util.List;
import javax.inject.Inject;
import javax.sql.DataSource;

import com.cagst.common.jdbc.BaseRepositoryJdbc;
import com.cagst.common.jdbc.StatementLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * A JDBC Implementation of the {@link UserRepository} interface.
 *
 * @author Craig Gaskill
 */
@Repository
/* package */ class UserRepositoryJdbc extends BaseRepositoryJdbc implements UserRepository {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserRepositoryJdbc.class);

  private static final String GET_USER_BY_USERNAME     = "GET_USER_BY_USERNAME";
  private static final String INCREMENT_LOGIN_ATTEMPTS = "INCREMENT_LOGIN_ATTEMPTS";
  private static final String LOGIN_SUCCESSFUL         = "LOGIN_SUCCESSFUL";
  private static final String USER_LOCK                = "USER_LOCK";
  private static final String USER_UNLOCK              = "USER_UNLOCK";

  private static final String GET_USERS = "GET_USERS";

  private final UserMapper USER_MAPPER = new UserMapper();

  /**
   * Primary Constructor used to create an instance of the <i>UserRepositoryJdbc</i> class.
   *
   * @param dataSource
   *     The {@link DataSource} used to retrieve / persist data objects.
   */
  @Inject
  protected UserRepositoryJdbc(DataSource dataSource) {
    super(dataSource);
  }

  @Override
  public Mono<UserEntity> getUserByUsername(@NonNull String username) {
    Assert.hasText(username, "Argument [username] cannot be null or empty");

    LOGGER.debug("Calling getUserByUsername [{}].", username);

    StatementLoader loader = StatementLoader.getLoader(getClass(), getStatementDialect());

    List<UserEntity> users = getJdbcTemplate().query(
        loader.load(GET_USER_BY_USERNAME),
        new MapSqlParameterSource("username", username),
        USER_MAPPER);

    if (users.size() == 1) {
      return Mono.just(users.get(0));
    } else if (users.size() == 0) {
      LOGGER.warn("User [{}] was not found!", username);
      return Mono.empty();
    } else {
      LOGGER.error("More than 1 user with username of [{}] was found!", username);
      return Mono.empty();
    }
  }

  @Override
  @Transactional
  public Mono<UserEntity> incrementLoginAttempts(Mono<UserEntity> user) {
    Assert.notNull(user, "Argument [user] cannot be null");

    StatementLoader loader = StatementLoader.getLoader(getClass(), getStatementDialect());

    return user.flatMap(usr -> {
      LOGGER.debug("Calling incrementLoginAttempts for User [{}].", usr.username());

      int cnt = getJdbcTemplate().update(loader.load(INCREMENT_LOGIN_ATTEMPTS), new MapSqlParameterSource("user_id", usr.userId()));
      if (cnt != 1L) {
        return Mono.error(new IncorrectResultSizeDataAccessException(1, cnt));
      } else {
        return Mono.just(usr.toBuilder()
            .loginAttempts(usr.loginAttempts() + 1)
            .build());
      }
    });
  }

  @Override
  @Transactional
  public Mono<UserEntity> loginSuccessful(Mono<UserEntity> user, String ipAddress) {
    Assert.notNull(user, "Argument [user] cannot be null");

    StatementLoader loader = StatementLoader.getLoader(getClass(), getStatementDialect());

    return user.flatMap(usr -> {
      LOGGER.debug("Calling loginSuccessful for User [{}].", usr.username());

      MapSqlParameterSource params = new MapSqlParameterSource();
      params.addValue("user_id", usr.userId());
      params.addValue("last_signin_ip", ipAddress);

      int cnt = getJdbcTemplate().update(loader.load(LOGIN_SUCCESSFUL), params);
      if (cnt != 1) {
        return Mono.error(new IncorrectResultSizeDataAccessException(1, cnt));
      } else {
        return Mono.just(usr.toBuilder().loginAttempts(0L).build());
      }
    });
  }

  @Override
  @Transactional
  public Mono<UserEntity> lockUserAccount(long userId, Mono<UserEntity> user) {
    Assert.notNull(user, "Argument [user] cannot be null");

    return user.flatMap(usr -> {
      LOGGER.debug("Calling lockUserAccount for User [{}].", usr.username());

      StatementLoader loader = StatementLoader.getLoader(getClass(), getStatementDialect());

      MapSqlParameterSource params = new MapSqlParameterSource();
      params.addValue("user_id", usr.userId());
      params.addValue("updt_id", userId);

      int cnt = getJdbcTemplate().update(loader.load(USER_LOCK), params);
      if (cnt != 1) {
        return Mono.error(new IncorrectResultSizeDataAccessException(1, cnt));
      } else {
        return Mono.just(usr.toBuilder().lockedDateTime(LocalDateTime.now()).build());
      }
    });
  }

  @Override
  @Transactional
  public Mono<UserEntity> unlockUserAccount(long userId, Mono<UserEntity> user) {
    Assert.notNull(user, "Argument [user] cannot be null");

    return user.flatMap(usr -> {
      LOGGER.debug("Calling unlockUserAccount for User [{}].", usr.username());

      StatementLoader loader = StatementLoader.getLoader(getClass(), getStatementDialect());

      MapSqlParameterSource params = new MapSqlParameterSource();
      params.addValue("user_id", usr.userId());
      params.addValue("updt_id", userId);

      int cnt = getJdbcTemplate().update(loader.load(USER_UNLOCK), params);
      if (cnt != 1) {
        return Mono.error(new IncorrectResultSizeDataAccessException(1, cnt));
      } else {
        return Mono.just(usr.toBuilder().lockedDateTime(null).loginAttempts(0).build());
      }
    });
  }

  @Override
  public Flux<UserEntity> getUsers() {
    LOGGER.debug("Calling getUsers");

    StatementLoader stmtLoader = StatementLoader.getLoader(getClass(), getStatementDialect());

    return Flux.fromIterable(getJdbcTemplate().query(stmtLoader.load(GET_USERS), USER_MAPPER));
  }
}
