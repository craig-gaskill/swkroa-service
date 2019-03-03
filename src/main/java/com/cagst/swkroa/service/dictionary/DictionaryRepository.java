package com.cagst.swkroa.service.dictionary;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Interface for retrieving / saving {@link Dictionary}s and {@link DictionaryValue}s from / to persistent storage.
 *
 * @author Craig Gaskill
 */
public interface DictionaryRepository {
  /**
   * Retrieves a {@link Flux} of {@link Dictionary} that are active within the system.
   *
   * @return A {@link Flux} of {@link Dictionary} that are active within the system.
   */
  Flux<Dictionary> getDictionaries();

  /**
   * Retrieves a {@link Dictionary} by its unique identifier.
   *
   * @param id
   *     The {@link long} that uniquely identifies the {@link Dictionary} to retrieve.
   *
   * @return The {@link Dictionary} that corresponds to the specified {@link long} unique identifier,
   * {@code Mono.empty} if the Dictionary does not exist.
   */
  Mono<Dictionary> getDictionaryById(long id);

  /**
   * Retrieves a {@link Dictionary} by its meaning.
   *
   * @param dictionaryType
   *    The {@link DictionaryType} that represents the meaning of the Dictionary to retrieve.
   *
   * @return The {@link Dictionary} that corresponds to the specified meaning, {@code Mode.empty} if
   * the Dictionary does not exist.
   */
  Mono<Dictionary> getDictionaryByType(DictionaryType dictionaryType);

  /**
   * Retrieves a {@link Flux} of a {@link DictionaryValue} that are associated to the {@link Dictionary} associated with the
   * specified type.
   *
   * @param dictionaryType
   *     The {@link DictionaryType} of the code set we want the code values for.
   *
   * @return A {@link Flux} of {@link DictionaryValue} that are associated to the specified {@link Dictionary} meaning, an empty
   * list if no CodeValues are associated to the Dictionary meaning.
   */
  Flux<DictionaryValue> getDictionaryValuesForDictionaryType(DictionaryType dictionaryType);

  /**
   * Retrieves a {@link Mono} of a {@link DictionaryValue} by its unique identifier.
   *
   * @param dictionaryType
   *    The {@link DictionaryType} of the code set we want the code values for.
   * @param dictionaryValueId
   *    The unique identifier of the DictionaryValue to retrieve.
   *
   * @return A {@link Mono} of the {@link DictionaryValue} if found, {@code Mono.empty} if not found.
   */
  Mono<DictionaryValue> getDictionaryValueById(DictionaryType dictionaryType, long dictionaryValueId);

  /**
   * Inserts a new {@link DictionaryValue} into persistent storage.
   *
   * @param userId
   *    The unique identifier of the User inserting the record.
   * @param dictionaryId
   *    The unique identifier of the Dictionary to insert the DictionaryValue for.
   * @param dictionaryValue
   *    The {@link DictionaryValue} to persist.
   *
   * @return The {@link DictionaryValue} after it has been persisted.
   */
  Mono<DictionaryValue> insertDictionaryValue(long userId, long dictionaryId, Mono<DictionaryValue> dictionaryValue);

  /**
   * Updates an existing {@link DictionaryValue} in persistent storage.
   *
   * @param userId
   *    The unique identifier of the User inserting the record.
   * @param dictionaryValue
   *    The {@link DictionaryValue} to persist.
   *
   * @return The {@link DictionaryValue} after it has been persisted.
   */
  Mono<DictionaryValue> updateDictionaryValue(long userId, Mono<DictionaryValue> dictionaryValue);
}
