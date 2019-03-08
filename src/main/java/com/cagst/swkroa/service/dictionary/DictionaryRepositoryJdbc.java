package com.cagst.swkroa.service.dictionary;

import java.util.List;
import javax.inject.Inject;
import javax.sql.DataSource;

import com.cagst.swkroa.service.internal.jdbc.BaseRepositoryJdbc;
import com.cagst.swkroa.service.internal.jdbc.StatementLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * JDBC implementation of the {@link DictionaryRepository} interface.
 *
 * @author Craig Gaskill
 */
@Repository
/* package */class DictionaryRepositoryJdbc extends BaseRepositoryJdbc implements DictionaryRepository {
  private static final Logger LOGGER = LoggerFactory.getLogger(DictionaryRepositoryJdbc.class);

  private static final String GET_DICTIONARIES                 = "GET_DICTIONARIES";
  private static final String GET_DICTIONARY_BY_ID             = "GET_DICTIONARY_BY_ID";
  private static final String GET_DICTIONARY_BY_MEANING        = "GET_DICTIONARY_BY_MEANING";
  private static final String GET_VALUES_BY_DICTIONARY_MEANING = "GET_VALUES_BY_DICTIONARY_MEANING";
  private static final String GET_VALUE_BY_ID                  = "GET_VALUE_BY_ID";

  private static final String INSERT_DICTIONARY_VALUE = "INSERT_DICTIONARY_VALUE";
  private static final String UPDATE_DICTIONARY_VALUE = "UPDATE_DICTIONARY_VALUE";

  private static final DictionaryMapper DICTIONARY_MAPPER = new DictionaryMapper();
  private static final DictionaryValueMapper DICTIONARY_VALUE_MAPPER = new DictionaryValueMapper();

  /**
   * Primary Constructor used to create an instance of the <i>DictionaryRepositoryJdbc</i>.
   *
   * @param dataSource
   *     The {@link DataSource} used to retrieve / persist data objects.
   */
  @Inject
  public DictionaryRepositoryJdbc(DataSource dataSource) {
    super(dataSource);
  }

  @Override
  public Flux<Dictionary> getDictionaries() {
    LOGGER.debug("Calling getDictionaries");

    StatementLoader stmtLoader = StatementLoader.getLoader(getClass(), getStatementDialect());

    return Flux.fromIterable(getJdbcTemplate().query(stmtLoader.load(GET_DICTIONARIES), DICTIONARY_MAPPER));
  }

  @Override
  public Mono<Dictionary> getDictionaryById(long id) {
    LOGGER.debug("Calling getDictionaryById for [{}]", id);

    StatementLoader stmtLoader = StatementLoader.getLoader(getClass(), getStatementDialect());

    List<Dictionary> dictionaries = getJdbcTemplate().query(
        stmtLoader.load(GET_DICTIONARY_BY_ID),
        new MapSqlParameterSource("dictionaryId", id),
        DICTIONARY_MAPPER);

    if (dictionaries.size() == 1) {
      return Mono.just(dictionaries.get(0));
    } else if (dictionaries.size() == 0) {
      LOGGER.warn("No dictionary with ID of [{}] was found!", id);
      return Mono.empty();
    } else {
      LOGGER.warn("More than 1 dictionary with ID of [{}] was found!", id);
      throw new IncorrectResultSizeDataAccessException(1, dictionaries.size());
    }
  }

  @Override
  public Mono<Dictionary> getDictionaryByType(DictionaryType dictionaryType) {
    LOGGER.debug("Calling getDictionaryByType for [{}]", dictionaryType);

    StatementLoader stmtLoader = StatementLoader.getLoader(getClass(), getStatementDialect());

    List<Dictionary> dictionaries = getJdbcTemplate().query(
        stmtLoader.load(GET_DICTIONARY_BY_MEANING),
        new MapSqlParameterSource("dictionaryMeaning", dictionaryType.name()),
        DICTIONARY_MAPPER);

    if (dictionaries.size() == 1) {
      return Mono.just(dictionaries.get(0));
    } else if (dictionaries.size() == 0) {
      LOGGER.warn("No dictionary with meaning of [{}] was found!", dictionaryType);
      return Mono.empty();
    } else {
      LOGGER.warn("More than 1 dictionary with meaning of [{}] was found!", dictionaryType);
      throw new IncorrectResultSizeDataAccessException(1, dictionaries.size());
    }
  }

  @Override
  public Flux<DictionaryValue> getDictionaryValuesForDictionaryType(DictionaryType dictionaryType) {
    Assert.notNull(dictionaryType, "Argument [dictionaryType] cannot be null.");

    LOGGER.debug("Calling getDictionaryValuesForDictionaryType for [{}]", dictionaryType);

    StatementLoader stmtLoader = StatementLoader.getLoader(getClass(), getStatementDialect());

    List<DictionaryValue> values = getJdbcTemplate().query(
        stmtLoader.load(GET_VALUES_BY_DICTIONARY_MEANING),
        new MapSqlParameterSource("dictionaryMeaning", dictionaryType.name()),
        DICTIONARY_VALUE_MAPPER);

    return Flux.fromIterable(values);
  }

  @Override
  public Mono<DictionaryValue> getDictionaryValueById(DictionaryType dictionaryType, long dictionaryValueId) {
    Assert.notNull(dictionaryType, "Argument [dictionaryType] cannot be null.");

    LOGGER.debug("Calling getDictionaryValueById for [{}, {}]", dictionaryType, dictionaryValueId);

    StatementLoader stmtLoader = StatementLoader.getLoader(getClass(), getStatementDialect());
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("dictionaryMeaning", dictionaryType.name());
    params.addValue("dictionaryValueId", dictionaryValueId);

    List<DictionaryValue> values = getJdbcTemplate().query(
        stmtLoader.load(GET_VALUE_BY_ID),
        params,
        DICTIONARY_VALUE_MAPPER);

    if (values.size() == 1) {
      return Mono.just(values.get(0));
    } else if (values.size() == 0) {
      LOGGER.warn("No dictionary with meaning of [{}] was found!", dictionaryType);
      return Mono.empty();
    } else {
      LOGGER.warn("More than 1 dictionary with meaning of [{}] was found!", dictionaryType);
      throw new IncorrectResultSizeDataAccessException(1, values.size());
    }
  }

  @Override
  public Mono<DictionaryValue> insertDictionaryValue(long userId, long dictionaryId, Mono<DictionaryValue> dictionaryValue) {
    Assert.notNull(dictionaryValue, "Argument dictionaryValue cannot be null.");

    return dictionaryValue.flatMap(dv -> {
      LOGGER.debug("Calling insertDictionaryValue for [{}]", dv.display());

      StatementLoader stmtLoader = StatementLoader.getLoader(getClass(), getStatementDialect());
      KeyHolder keyHolder = new GeneratedKeyHolder();

      int cnt = getJdbcTemplate().update(
          stmtLoader.load(INSERT_DICTIONARY_VALUE),
          DictionaryValueMapper.mapForInsert(userId, dictionaryId, dv),
          keyHolder);

      if (cnt != 1) {
        throw new IncorrectResultSizeDataAccessException(1, cnt);
      }

      return Mono.just(dv.toBuilder()
          .dictionaryValueId(keyHolder.getKey().longValue())
          .build());
    });
  }

  @Override
  public Mono<DictionaryValue> updateDictionaryValue(long userId, Mono<DictionaryValue> dictionaryValue) {
    Assert.notNull(dictionaryValue, "Argument dictionaryValue cannot be null.");

    return dictionaryValue.flatMap(dv -> {
      LOGGER.debug("Calling updateDictionaryValue for [{}]", dv.display());

      StatementLoader stmtLoader = StatementLoader.getLoader(getClass(), getStatementDialect());

      int cnt = getJdbcTemplate().update(
          stmtLoader.load(UPDATE_DICTIONARY_VALUE),
          DictionaryValueMapper.mapForUpdate(userId, dv));

      if (cnt == 1) {
        return Mono.just(dv.toBuilder()
            .updateCount(dv.updateCount() + 1)
            .build());
      } else if (cnt == 0) {
        throw new OptimisticLockingFailureException("invalid update count of [" + cnt
            + "] possible update count mismatch");
      } else {
        throw new IncorrectResultSizeDataAccessException(1, cnt);
      }
    });
  }
}
