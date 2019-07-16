package com.cagst.swkroa.service.dictionary;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Test class for the {@link DictionaryServiceImpl} class.
 *
 * @author Craig Gaskill
 */
@DisplayName("DictionaryServiceImpl")
class DictionaryServiceImplTest {
  private DictionaryRepository dictionaryRepository = mock(DictionaryRepository.class);

  private DictionaryServiceImpl service = new DictionaryServiceImpl(dictionaryRepository);

  @Nested
  @DisplayName("when getting dictionaries")
  class WhenGetDictionaries {
    @Test
    @DisplayName("should return an empty collection when none are found")
    void testNoneFound() {
      when(dictionaryRepository.getDictionaries()).thenReturn(Flux.empty());

      service.getDictionaries()
          .collectList()
          .subscribe(dictionaries -> assertAll("Ensure the list of dictionaries",
              () -> assertNotNull(dictionaries, "is valid"),
              () -> assertTrue(dictionaries.isEmpty(), "is empty (none were found)")));
    }

    @Test
    @DisplayName("should return a populated collection when found")
    void testFound() {
      Dictionary d1 = Dictionary.builder()
          .dictionaryId(1L)
          .display("Address Type")
          .meaning("ADDRESS_TYPE")
          .build();
      Dictionary d2 = Dictionary.builder()
          .dictionaryId(2L)
          .display("Phone Type")
          .meaning("PHONE_TYPE")
          .build();

      when(dictionaryRepository.getDictionaries()).thenReturn(Flux.fromIterable(Arrays.asList(d1, d2)));

      service.getDictionaries()
          .collectList()
          .subscribe(dictionaries -> assertAll("Ensure the list of dictionaries",
              () -> assertNotNull(dictionaries, "is valid"),
              () -> assertFalse(dictionaries.isEmpty(), "is not empty (was found)"),
              () -> assertEquals(2, dictionaries.size(), "has the correct number of dictionaries")));
    }
  }

  @Nested
  @DisplayName("when getting a specific dictionary")
  class WhenGetDictionary {
    @Nested
    @DisplayName("by ID")
    class ById {
      @Test
      @DisplayName("should return an empty optional when not found")
      void testNotFound() {
        when(dictionaryRepository.getDictionaryById(anyLong())).thenReturn(Mono.empty());

        service.getDictionaryById(99L)
            .subscribe(dictionary -> assertNull(dictionary, "Ensure the dictionary was not found"));
      }

      @Test
      @DisplayName("should return a populated optional when found")
      void testFound() {
        Dictionary d1 = Dictionary.builder()
            .dictionaryId(1L)
            .display("Address Type")
            .meaning("ADDRESS_TYPE")
            .build();

        when(dictionaryRepository.getDictionaryById(anyLong())).thenReturn(Mono.just(d1));

        service.getDictionaryById(99L)
            .subscribe(dictionary -> assertAll("Ensure the dictionary",
                () -> assertNotNull(dictionary, "was found"),
                () -> assertEquals("ADDRESS_TYPE", dictionary.meaning(), "is the correct dictionary")));
      }
    }

    @Nested
    @DisplayName("by Type")
    class ByType {
      @Test
      @DisplayName("should return an empty optional when not found")
      void testNotFound() {
        when(dictionaryRepository.getDictionaryByType(any(DictionaryType.class))).thenReturn(Mono.empty());

        service.getDictionaryByType(DictionaryType.ADDRESS_TYPE)
            .subscribe(dictionary -> assertNull(dictionary, "Ensure the dictionary was not found"));
      }

      @Test
      @DisplayName("should return a populated optional when found")
      void testFound() {
        Dictionary d1 = Dictionary.builder()
            .dictionaryId(1L)
            .display("Address Type")
            .meaning("ADDRESS_TYPE")
            .build();

        when(dictionaryRepository.getDictionaryByType(DictionaryType.ADDRESS_TYPE)).thenReturn(Mono.just(d1));

        service.getDictionaryByType(DictionaryType.ADDRESS_TYPE)
            .subscribe(dictionary -> assertAll("Ensure the dictionary",
                () -> assertNotNull(dictionary, "was found"),
                () -> assertEquals("ADDRESS_TYPE", dictionary.meaning(), "is the correct dictionary")));
      }
    }
  }

  @Nested
  @DisplayName("when getting dictionary values")
  class WhenGetDictionaryValues {
    @Nested
    @DisplayName("for a dictionary type")
    class ForDictionaryType {
      @Test
      @DisplayName("should return an empty collection when none found")
      void testNoneFound() {
        when(dictionaryRepository.getDictionaryValuesForDictionaryType(any(DictionaryType.class)))
            .thenReturn(Flux.empty());

        service.getDictionaryValuesForDictionaryType(DictionaryType.ADDRESS_TYPE)
            .collectList()
            .subscribe(values -> assertAll("Ensure the list of values",
                () -> assertNotNull(values, "is valid"),
                () -> assertTrue(values.isEmpty(), "is empty (were not found)")));
      }

      @Test
      @DisplayName("should return a populated collection when found")
      void testFound() {
        DictionaryValue v1 = DictionaryValue.builder()
            .dictionaryValueId(1L)
            .display("Home")
            .meaning("HOME")
            .build();
        DictionaryValue v2 = DictionaryValue.builder()
            .dictionaryValueId(2L)
            .display("Work")
            .meaning("WORK")
            .build();

        when(dictionaryRepository.getDictionaryValuesForDictionaryType(any(DictionaryType.class)))
            .thenReturn(Flux.fromIterable(Arrays.asList(v1, v2)));

        service.getDictionaryValuesForDictionaryType(DictionaryType.ADDRESS_TYPE)
            .collectList()
            .subscribe(values -> assertAll("Ensure the list of values",
                () -> assertNotNull(values, "is valid"),
                () -> assertFalse(values.isEmpty(), "is not empty (was found)"),
                () -> assertEquals(2, values.size(), "has the correct number of values")));
      }
    }
  }

  @Nested
  @DisplayName("when inserting a dictionary value")
  class WhenCreateDictionaryValue {
    @Test
    @DisplayName("should ")
    void testDuplicate() {

    }

    @Test
    @DisplayName("should insert the dictionary value")
    void testInsert() {
      DictionaryValue newValue = DictionaryValue.builder()
          .display("New")
          .meaning("NEW")
          .build();

      DictionaryValue insertedValue = newValue.toBuilder().dictionaryValueId(101L).build();
      when(dictionaryRepository.insertDictionaryValue(anyLong(), anyLong(), eq(Mono.just(newValue))))
          .thenReturn(Mono.just(insertedValue));

//      service.insertDictionaryValue(1L, DictionaryType.ADDRESS_TYPE, Mono.just(newValue))
//          .subscribe(inserted -> assertAll("Ensure the dictionary value"),
//              () -> assertNotNull(inserted, "was inserted"));
    }
  }

  @Nested
  @DisplayName("when updating a dictionary value")
  class WhenUpdateDictionaryValue {

  }

  @Nested
  @DisplayName("when deleting a dictionary value")
  class WhenDeleteDictionaryValue {

  }
}
