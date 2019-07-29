package com.cagst.swkroa.service.dictionary;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Interface for retrieving / saving {@link Dictionary}s from / to persistent storage.
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
}
