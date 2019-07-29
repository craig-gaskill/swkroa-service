package com.cagst.swkroa.service.dictionary;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.util.List;

import com.cagst.common.jdbc.BaseRepositoryJdbc;
import com.cagst.common.jdbc.StatementLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;
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

  private static final DictionaryMapper DICTIONARY_MAPPER = new DictionaryMapper();

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
}
