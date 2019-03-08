package com.cagst.swkroa.service.dictionary;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Optional;

import com.cagst.swkroa.service.internal.jdbc.BaseTestRepository;
import com.cagst.swkroa.service.internal.jdbc.StatementDialect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.dao.OptimisticLockingFailureException;
import reactor.core.publisher.Mono;

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
          .subscribe(dictionaries -> assertAll("Ensure the list of dictionaries",
              () -> assertNotNull(dictionaries, "is valid"),
              () -> assertFalse(dictionaries.isEmpty(), "is not empty"),
              () -> assertEquals(5, dictionaries.size(), "has the correct number of dictionaries")
          ));
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
            .subscribe(dictionary -> assertAll("Ensure the dictionary",
                () -> assertNotNull(dictionary, "is valid"),
                () -> assertEquals("EMAIL_TYPE", dictionary.meaning(), "is the correct dictionary")));
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

  @Nested
  @DisplayName("when getting dictionary values")
  class WhenGetDictionaryValues {
    @Test
    @DisplayName("should return an empty collection when none are found")
    void testNoneFound() {
      repo.getDictionaryValuesForDictionaryType(DictionaryType.SECURITY_QUESTION)
          .collectList()
          .subscribe(dictionaryValues -> assertAll("Ensure the list dictionary values",
              () -> assertNotNull(dictionaryValues, "is valid"),
              () -> assertTrue(dictionaryValues.isEmpty(), "is empty (none were found)")));
    }

    @Test
    @DisplayName("should return a populated collection when found")
    void testFound() {
      repo.getDictionaryValuesForDictionaryType(DictionaryType.PHONE_TYPE)
          .collectList()
          .subscribe(dictionaryValues -> assertAll("Ensure the list dictionary values",
              () -> assertNotNull(dictionaryValues, "is valid"),
              () -> assertFalse(dictionaryValues.isEmpty(), "is not empty (was found)"),
              () -> assertEquals(3, dictionaryValues.size(), "has the correct number of values")));
    }
  }

  @Nested
  @DisplayName("when getting a specific dictionary value")
  class WhenGetDictionaryValue {
    @Test
    @DisplayName("should return an empty mono when not found")
    void testNotFound() {
      repo.getDictionaryValueById(DictionaryType.PHONE_TYPE, 999L)
          .subscribe(value -> assertNotNull(value, "Ensure the dictionary value was not found"));
    }

    @Test
    @DisplayName("should return a populated mono when found")
    void testFound() {
      repo.getDictionaryValueById(DictionaryType.PHONE_TYPE, 3L)
          .subscribe(value -> assertAll("Ensure the dictionary value",
              () -> assertNotNull(value, "was found"),
              () -> assertEquals("HOME", value.meaning(), "is the correct value")));
    }
  }

  @Nested
  @DisplayName("when inserting a new dictionary value")
  class WhenInsertDictionaryValue {
    @Test
    @DisplayName("should insert the new dictionary value")
    void testInsert() {
      Optional<Dictionary> dictionary = repo.getDictionaryByType(DictionaryType.PHONE_TYPE).blockOptional();

      repo.getDictionaryValuesForDictionaryType(DictionaryType.PHONE_TYPE)
          .collectList()
          .subscribe(dictionaryValues -> assertAll("Ensure the list dictionary values",
              () -> assertNotNull(dictionaryValues, "is valid"),
              () -> assertFalse(dictionaryValues.isEmpty(), "is not empty (was found)"),
              () -> assertEquals(3, dictionaryValues.size(), "has the correct number of values")));

      DictionaryValue pager = DictionaryValue.builder().meaning("PAGER").display("Pager").build();

      repo.insertDictionaryValue(1L, dictionary.get().dictionaryId(), Mono.just(pager))
          .subscribe(value -> {
            assertAll("Ensure the dictionary value",
                () -> assertNotNull(value, "was inserted"),
                () -> assertNotNull(value.dictionaryValueId(), "has a valid ID"));
          });

      repo.getDictionaryValuesForDictionaryType(DictionaryType.PHONE_TYPE)
          .collectList()
          .subscribe(dictionaryValues -> assertAll("Ensure the list dictionary values",
              () -> assertNotNull(dictionaryValues, "is valid"),
              () -> assertFalse(dictionaryValues.isEmpty(), "is not empty (was found)"),
              () -> assertEquals(4, dictionaryValues.size(), "has the correct number of values")));
    }
  }

  @Nested
  @DisplayName("when updating an existing dictionary value")
  class WhenUpdateDictionaryValue {
    @Test
    @DisplayName("should update an existing dictionary value")
    void testUpdate() {
      Optional<DictionaryValue> dv1 = repo.getDictionaryValueById(DictionaryType.PHONE_TYPE, 3L)
          .blockOptional();

      DictionaryValue dv = dv1.get().toBuilder().display("Home / Residence").build();

      repo.updateDictionaryValue(1L, Mono.just(dv))
          .subscribe(value -> assertAll("Ensure the dictionary value",
              () -> assertNotNull(value, "is valid"),
              () -> assertEquals("Home / Residence", value.display(), "is the updated value"),
              () -> assertEquals(dv.updateCount() + 1, value.updateCount(), "was updated")));
    }

    @Test
    @DisplayName("should throw an OptimisticLockingFailure due to an update count mismatch")
    @Disabled
    void testFailed() {
      Optional<DictionaryValue> dv1 = repo.getDictionaryValueById(DictionaryType.PHONE_TYPE, 3L)
          .blockOptional();

      DictionaryValue dv = dv1.get().toBuilder().display("Home / Residence").updateCount(dv1.get().updateCount() + 1).build();

      assertThrows(OptimisticLockingFailureException.class, () -> repo.updateDictionaryValue(1L, Mono.just(dv))
          .subscribe(value -> fail())
      );
    }
  }
}
