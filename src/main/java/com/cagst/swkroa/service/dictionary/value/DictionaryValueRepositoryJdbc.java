package com.cagst.swkroa.service.dictionary.value;

import javax.sql.DataSource;
import java.util.List;

import com.cagst.common.jdbc.BaseRepositoryJdbc;
import com.cagst.common.jdbc.StatementLoader;
import com.cagst.swkroa.service.dictionary.DictionaryType;
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
 * JDBC implementation of the {@link DictionaryValueRepository} interface.
 *
 * @author Craig Gaskill
 */
@Repository
/* package */ class DictionaryValueRepositoryJdbc extends BaseRepositoryJdbc implements DictionaryValueRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(DictionaryValueRepositoryJdbc.class);

    private static final String GET_VALUES_BY_DICTIONARY_MEANING = "GET_VALUES_BY_DICTIONARY_MEANING";
    private static final String GET_VALUE_BY_ID                  = "GET_VALUE_BY_ID";

    private static final String INSERT_DICTIONARY_VALUE = "INSERT_DICTIONARY_VALUE";
    private static final String UPDATE_DICTIONARY_VALUE = "UPDATE_DICTIONARY_VALUE";

    private static final DictionaryValueMapper DICTIONARY_VALUE_MAPPER = new DictionaryValueMapper();

    /**
     * Primary Constructor used to create an instance of the <i>Dictionary</i>.
     *
     * @param dataSource
     *      The {@link DataSource} used to retrieve / persist data objects.
     */
    public DictionaryValueRepositoryJdbc(DataSource dataSource) {
        super(dataSource);
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

            return Mono.just(new DictionaryValue.Builder()
                .from(dv)
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
                return Mono.just(new DictionaryValue.Builder()
                    .from(dv)
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
