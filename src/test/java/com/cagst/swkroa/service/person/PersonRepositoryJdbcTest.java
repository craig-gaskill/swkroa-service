package com.cagst.swkroa.service.person;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import com.cagst.common.jdbc.BaseTestRepository;
import com.cagst.common.jdbc.StatementDialect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Test class for the {@link PersonRepositoryJdbc} class.
 *
 * @author Craig Gaskill
 */
@DisplayName("PersonRepositoryJdbc")
class PersonRepositoryJdbcTest extends BaseTestRepository {
    private PersonRepositoryJdbc repo;

    @BeforeEach
    void setUp() {
        repo = new PersonRepositoryJdbc(createTestDataSource());
        repo.setStatementDialect(StatementDialect.HSQLDB);
    }

    @Nested
    @DisplayName("when getting People by IDs")
    class WhenGetPeopleByIds {
        @Test
        @DisplayName("should return an empty flux when no People are found")
        void testNoneFound() {
            Set<Long> personIds = new HashSet<>();
            personIds.add(999L);

            repo.getPeopleByIds(personIds)
                .collectList()
                .subscribe(results ->
                    assertAll("Ensure the list of people",
                        () -> assertNotNull(results, "is valid"),
                        () -> assertTrue(results.isEmpty(), "is empty")));
        }

        @Test
        @DisplayName("should return a populated (emitting) flux when People are found")
        void testFound() {
            Set<Long> personIds = new HashSet<>();
            personIds.add(1L);
            personIds.add(10L);
            personIds.add(11L);
            personIds.add(999L);

            repo.getPeopleByIds(personIds)
                .collectList()
                .subscribe(results ->
                    assertAll("Ensure the list of people",
                        () -> assertNotNull(results, "is valid"),
                        () -> assertFalse(results.isEmpty(), "is not empty"),
                        () -> assertEquals(3, results.size(), "has the correct number of people")));
        }
    }
}
