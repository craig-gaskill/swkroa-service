package com.cagst.swkroa.service.dictionary;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Defines methods that enforce the business rules for retrieving and persisting {@link Dictionary} records.
 *
 * @author Craig Gaskill
 */
public interface DictionaryService {
  /**
   * Retrieves a {@link Flux} of {@link Dictionary}.
   *
   * @return A {@link Flux} of {@link Dictionary}.
   */
  Flux<Dictionary> getDictionaries();

  /**
   * Retrieves a {@link Dictionary} by its unique identifier.
   *
   * @param id
   *     The {@link long} that uniquely identifies the {@link Dictionary} to retrieve.
   *
   * @return The {@link Dictionary} that corresponds to the specified unique identifier, {@code Mono.empty} if
   * the Dictionary does not exist.
   */
  Mono<Dictionary> getDictionaryById(long id);

  /**
   * Retrieves a {@link Dictionary} by its type.
   *
   * @param dictionaryType
   *    The {@link DictionaryType} that identifies the {@link Dictionary} to retrieve.
   *
   * @return The {@link Dictionary} that corresponds to the specified type, {@code Mono.empty} if
   * the Dictionary does not exist.
   */
  Mono<Dictionary> getDictionaryByType(DictionaryType dictionaryType);

}
