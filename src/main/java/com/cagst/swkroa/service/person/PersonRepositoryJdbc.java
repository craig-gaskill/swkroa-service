package com.cagst.swkroa.service.person;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.cagst.common.jdbc.BaseRepositoryJdbc;
import com.cagst.common.jdbc.StatementLoader;
import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;

@Repository
/* package */ class PersonRepositoryJdbc extends BaseRepositoryJdbc implements PersonRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonRepositoryJdbc.class);

    private static final String GET_PEOPLE_BY_IDS = "GET_PEOPLE_BY_IDS";

    private final PersonMapper PERSON_MAPPER = new PersonMapper();

    /**
     * Primary Constructor used to create an instance of the <i>PersonRepositoryJdbc</i> class.
     *
     * @param dataSource
     *      The {@link DataSource} used to retrieve / persist data objects.
     */
    @Inject
    protected PersonRepositoryJdbc(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Flux<Person> getPeopleByIds(Collection<Long> personIds) {
        Assert.notEmpty(personIds, "Argument [personIds] cannot be null or empty.");

        LOGGER.debug("Calling getPeopleByIds for [{}].", personIds);

        List<List<Long>> partitionedList = ListUtils.partition(List.copyOf(personIds), 1000);

        StatementLoader stmtLoader = StatementLoader.getLoader(getClass(), getStatementDialect());
        String query = stmtLoader.load(GET_PEOPLE_BY_IDS);

        List<Person> results = new ArrayList<>(personIds.size());
        for (List<Long> list : partitionedList) {
            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("personIds", list);

            results.addAll(getJdbcTemplate().query(query, params, PERSON_MAPPER));
        }

        return Flux.fromIterable(results);
    }
}
