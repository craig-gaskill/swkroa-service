package com.cagst.swkroa.service.dictionary;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Optional;

import com.cagst.common.jdbc.BaseTestRepository;
import com.cagst.common.jdbc.StatementDialect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Test class for the {@link DictionaryRepositoryJdbc} class.
 *
 * @author Craig Gaskill
 */
@DisplayName("DictionaryRepositoryJdbc")
class DictionaryRepositoryJdbcTest extends BaseTestRepository {
  private DictionaryRepositoryJdbc repo;

  @BeforeEach
  void setUp() {
    repo = new DictionaryRepositoryJdbc(createTestDataSource());
    repo.setStatementDialect(StatementDialect.HSQLDB);
  }

  @Nested
  @DisplayName("when getting dictionaries")
  class WhenGetDictionaries {
    @Test
    @DisplayName("should return a list of dictionaries")
    void testGetDictionaries() {
      repo.getDictionaries()
          .collectList()
          .subscribe(dictionaries ->
              assertAll("Ensure the list of dictionaries",
                  () -> assertNotNull(dictionaries, "is valid"),
                  () -> assertFalse(dictionaries.isEmpty(), "is not empty"),
                  () -> assertEquals(5, dictionaries.size(), "has the correct number of dictionaries"))
          );
    }
  }

  @Nested
  @DisplayName("when getting a specific dictionary")
  class WhenGetDictionary {
    @Nested
    @DisplayName("by ID")
    class ById {
      @Test
      @DisplayName("should return an empty mono when not found")
      void testNotFound() {
        repo.getDictionaryById(999L)
            .subscribe(dictionary -> fail("should not emit an event since the token was not found"));
      }

      @Test
      @DisplayName("should return a populated mono when found")
      void testFound() {
        repo.getDictionaryById(3L)
            .subscribe(dictionary ->
                assertAll("Ensure the dictionary",
                    () -> assertNotNull(dictionary, "is valid"),
                    () -> assertEquals("EMAIL_TYPE", dictionary.meaning(), "is the correct dictionary"))
            );
      }
    }

    @Nested
    @DisplayName("by Type")
    class ByType {
      @Test
      @DisplayName("should return an empty mono when not found")
      void testNotFound() {
        Optional<Dictionary> dv = repo.getDictionaryByType(DictionaryType.SECURITY_QUESTION).blockOptional();

        assertAll("Ensure the Dictionary Value",
            () -> assertNotNull(dv, "is valid"),
            () -> assertFalse(dv.isPresent(), "is not presents (was not found)"));
      }

      @Test
      @DisplayName("should return a populated mono when found")
      void testFound() {
        Optional<Dictionary> dv = repo.getDictionaryByType(DictionaryType.EMAIL_TYPE).blockOptional();

        assertAll("Ensure the dictionary",
            () -> assertNotNull(dv, "is valid"),
            () -> assertTrue(dv.isPresent(), "is presents (was found)"),
            () -> assertEquals("EMAIL_TYPE", dv.get().meaning(), "is the correct dictionary"));
      }
    }
  }
}
