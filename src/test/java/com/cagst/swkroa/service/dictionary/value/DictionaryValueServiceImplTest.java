package com.cagst.swkroa.service.dictionary.value;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import com.cagst.swkroa.service.dictionary.DictionaryRepository;
import com.cagst.swkroa.service.dictionary.DictionaryType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Test class for the {@link DictionaryValueServiceImpl} class.
 *
 * @author Craig Gaskill
 */
@DisplayName("DictionaryValueServiceImpl")
class DictionaryValueServiceImplTest {
    private DictionaryRepository dictionaryRepository = mock(DictionaryRepository.class);
    private DictionaryValueRepository dictionaryValueRepository = mock(DictionaryValueRepository.class);

    private DictionaryValueServiceImpl service = new DictionaryValueServiceImpl(dictionaryRepository, dictionaryValueRepository);

    @Nested
    @DisplayName("when getting dictionary values")
    class WhenGetDictionaryValues {
        @Nested
        @DisplayName("for a dictionary type")
        class ForDictionaryType {
            @Test
            @DisplayName("should return an empty collection when none found")
            void testNoneFound() {
                when(dictionaryValueRepository.getDictionaryValuesForDictionaryType(any(DictionaryType.class)))
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
                DictionaryValue v1 = new DictionaryValue.Builder()
                    .dictionaryValueId(1L)
                    .display("Home")
                    .meaning("HOME")
                    .build();
                DictionaryValue v2 = new DictionaryValue.Builder()
                    .dictionaryValueId(2L)
                    .display("Work")
                    .meaning("WORK")
                    .build();

                when(dictionaryValueRepository.getDictionaryValuesForDictionaryType(any(DictionaryType.class)))
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
            DictionaryValue newValue = new DictionaryValue.Builder()
                .display("New")
                .meaning("NEW")
                .build();

            DictionaryValue insertedValue = new DictionaryValue.Builder().from(newValue).dictionaryValueId(101L).build();
            when(dictionaryValueRepository.insertDictionaryValue(anyLong(), anyLong(), eq(Mono.just(newValue))))
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
