package com.cagst.swkroa.service.dictionary.value;

import com.cagst.swkroa.service.dictionary.Dictionary;
import com.cagst.swkroa.service.dictionary.DictionaryType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Defines methods that enforce the business rules for retrieving and persisting {@link DictionaryValue} records.
 *
 * @author Craig Gaskill
 */
public interface DictionaryValueService {
    /**
     * Retrieves a {@link Flux} of {@link DictionaryValue} that are associated to the {@link Dictionary}
     * associated with the specified type.
     *
     * @param dictionaryType
     *     The {@link DictionaryType} of the code set we want the code values for.
     *
     * @return A {@link Flux} of {@link DictionaryValue} that are associated to the specified {@link Dictionary} meaning,
     * an empty list if no CodeValues are associated to the Dictionary meaning.
     */
    Flux<DictionaryValue> getDictionaryValuesForDictionaryType(DictionaryType dictionaryType);

    /**
     * Inserts a new {@link DictionaryValue} into persistent storage.
     *
     * @param userId
     *    The unique identifier of the User inserting the record.
     * @param dictionaryType
     *     The {@link DictionaryType} of the code set we want the code values for.
     * @param dictionaryValue
     *    The {@link DictionaryValue} to persist.
     *
     * @return The {@link DictionaryValue} after it has been persisted.
     */
    Mono<DictionaryValue> insertDictionaryValue(long userId,
                                                DictionaryType dictionaryType,
                                                Mono<DictionaryValue> dictionaryValue);

    /**
     * Updates an existing {@link DictionaryValue} in persistent storage.
     *
     * @param userId
     *    The unique identifier of the User updating the record.
     * @param dictionaryType
     *     The {@link DictionaryType} of the code set we want the code values for.
     * @param dictionaryValueId
     *    The unique identifier of the DictionaryValue to update.
     * @param dictionaryValue
     *    The {@link DictionaryValue} to persist.
     *
     * @return The {@link DictionaryValue} after it has been persisted.
     */
    Mono<DictionaryValue> updateDictionaryValue(long userId,
                                                DictionaryType dictionaryType,
                                                long dictionaryValueId,
                                                Mono<DictionaryValue> dictionaryValue);

    /**
     * Deletes an existing {@link DictionaryValue} in persistent storage.
     *
     * @param userId
     *    The unique identifier of the User updating the record.
     * @param dictionaryType
     *     The {@link DictionaryType} of the code set we want the code values for.
     * @param dictionaryValueId
     *    The unique identifier of the DictionaryValue to update.
     */
    Mono<Void> deleteDictionaryValue(long userId,
                                     DictionaryType dictionaryType,
                                     long dictionaryValueId);
}
