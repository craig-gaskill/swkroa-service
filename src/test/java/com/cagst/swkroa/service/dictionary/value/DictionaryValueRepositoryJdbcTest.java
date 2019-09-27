package com.cagst.swkroa.service.dictionary.value;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Optional;

import com.cagst.common.jdbc.BaseTestRepository;
import com.cagst.common.jdbc.StatementDialect;
import com.cagst.swkroa.service.dictionary.DictionaryType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.dao.OptimisticLockingFailureException;
import reactor.core.publisher.Mono;

/**
 * Test class for the {@link DictionaryValueRepositoryJdbc} class.
 *
 * @author Craig Gaskill
 */
@DisplayName("DictionaryValueRepositoryJdbc")
class DictionaryValueRepositoryJdbcTest extends BaseTestRepository {
  private DictionaryValueRepositoryJdbc repo;

  @BeforeEach
  void setUp() {
    repo = new DictionaryValueRepositoryJdbc(createTestDataSource());
    repo.setStatementDialect(StatementDialect.HSQLDB);
  }

  @Nested
  @DisplayName("when getting dictionary values")
  class WhenGetDictionaryValues {
    @Test
    @DisplayName("should return an empty collection when none are found")
    void testNoneFound() {
      repo.getDictionaryValuesForDictionaryType(DictionaryType.SECURITY_QUESTION)
          .collectList()
          .subscribe(dictionaryValues ->
              assertAll("Ensure the list dictionary values",
                  () -> assertNotNull(dictionaryValues, "is valid"),
                  () -> assertTrue(dictionaryValues.isEmpty(), "is empty (none were found)"))
          );
    }

    @Test
    @DisplayName("should return a populated collection when found")
    void testFound() {
      repo.getDictionaryValuesForDictionaryType(DictionaryType.PHONE_TYPE)
          .collectList()
          .subscribe(dictionaryValues ->
              assertAll("Ensure the list dictionary values",
                  () -> assertNotNull(dictionaryValues, "is valid"),
                  () -> assertFalse(dictionaryValues.isEmpty(), "is not empty (was found)"),
                  () -> assertEquals(3, dictionaryValues.size(), "has the correct number of values"))
          );
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
          .subscribe(value ->
              assertAll("Ensure the dictionary value",
                  () -> assertNotNull(value, "was found"),
                  () -> assertEquals("HOME", value.meaning(), "is the correct value"))
          );
    }
  }

  @Nested
  @DisplayName("when inserting a new dictionary value")
  class WhenInsertDictionaryValue {
    @Test
    @DisplayName("should insert the new dictionary value")
    void testInsert() {
      repo.getDictionaryValuesForDictionaryType(DictionaryType.PHONE_TYPE)
          .collectList()
          .subscribe(dictionaryValues ->
              assertAll("Ensure the list dictionary values",
                  () -> assertNotNull(dictionaryValues, "is valid"),
                  () -> assertFalse(dictionaryValues.isEmpty(), "is not empty (was found)"),
                  () -> assertEquals(3, dictionaryValues.size(), "has the correct number of values"))
          );

      DictionaryValue pager = new DictionaryValue.Builder().meaning("PAGER").display("Pager").build();

      repo.insertDictionaryValue(1L, 2L, Mono.just(pager))
          .subscribe(value ->
              assertAll("Ensure the dictionary value",
                  () -> assertNotNull(value, "was inserted"),
                  () -> assertNotNull(value.dictionaryValueId(), "has a valid ID"))
          );

      repo.getDictionaryValuesForDictionaryType(DictionaryType.PHONE_TYPE)
          .collectList()
          .subscribe(dictionaryValues ->
              assertAll("Ensure the list dictionary values",
                  () -> assertNotNull(dictionaryValues, "is valid"),
                  () -> assertFalse(dictionaryValues.isEmpty(), "is not empty (was found)"),
                  () -> assertEquals(4, dictionaryValues.size(), "has the correct number of values"))
          );
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

      DictionaryValue dv = new DictionaryValue.Builder().from(dv1.get()).display("Home / Residence").build();

      repo.updateDictionaryValue(1L, Mono.just(dv))
          .subscribe(value ->
              assertAll("Ensure the dictionary value",
                  () -> assertNotNull(value, "is valid"),
                  () -> assertEquals("Home / Residence", value.display(), "is the updated value"),
                  () -> assertEquals(dv.updateCount() + 1, value.updateCount(), "was updated"))
          );
    }

    @Test
    @DisplayName("should throw an OptimisticLockingFailure due to an update count mismatch")
    @Disabled
    void testFailed() {
      Optional<DictionaryValue> dv1 = repo.getDictionaryValueById(DictionaryType.PHONE_TYPE, 3L)
          .blockOptional();

      DictionaryValue dv = new DictionaryValue.Builder().from(dv1.get()).display("Home / Residence").updateCount(dv1.get().updateCount() + 1).build();

      assertThrows(OptimisticLockingFailureException.class, () -> repo.updateDictionaryValue(1L, Mono.just(dv))
          .subscribe(value -> fail())
      );
    }
  }
}
