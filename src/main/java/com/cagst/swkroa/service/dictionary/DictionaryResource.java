package com.cagst.swkroa.service.dictionary;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Handles endpoints for retrieving and persisting {@link Dictionary} and {@link DictionaryValue} objects.
 *
 * @author Craig Gaskill
 */
@RestController
@RequestMapping(value = "dictionaries", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class DictionaryResource {
  private static final Logger LOGGER = LoggerFactory.getLogger(DictionaryResource.class);

  private final DictionaryService dictionaryService;

  @Inject
  public DictionaryResource(DictionaryService dictionaryService) {
    this.dictionaryService = dictionaryService;
  }

  /**
   * Handles the request to retrieve the {@link Dictionary}s within the system.
   *
   * @return A JSON representation of the {@link Dictionary} within the system.
   */
  @GetMapping
  public Flux<Dictionary> getDictionaries() {
    LOGGER.debug("Received request to getDictionaries");

    return dictionaryService.getDictionaries();
  }

  /**
   * Handles the request to retrieve a specific {@link Dictionary} by its unique identifier.
   *
   * @param id
   *    The unique identifier of the Dictionary to retrieve.
   *
   * @return A JSON representation of the {@link Dictionary}.
   */
  @GetMapping("{id}")
  public Mono<ResponseEntity<Dictionary>> getDictionary(@PathVariable long id) {
    Assert.isTrue(id > 0, "Argument [id] must be greater than 0.");

    LOGGER.debug("Received request to getDictionary for [{}]", id);

    return dictionaryService.getDictionaryById(id)
        .map(ResponseEntity::ok)
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  /**
   * Handles the request and retrieves the {@link DictionaryValue} associated to the specified Dictionary.
   *
   * @param dictionaryType
   *    A {@link DictionaryType} that represents the {@link Dictionary} to retrieve {@link DictionaryValue} for.
   *
   * @return A JSON representation of the DictionaryValue associated to the specified Dictionary.
   */
  @GetMapping("{dictionaryType}/values")
  public Flux<DictionaryValue> getDictionaryValues(@PathVariable DictionaryType dictionaryType) {
    Assert.notNull(dictionaryType, "Argument [dictionaryType] cannot be null.");

    LOGGER.debug("Received request to getDictionaryValues for [{}]", dictionaryType);

    return dictionaryService.getDictionaryValuesForDictionaryType(dictionaryType);
  }

  /**
   * Handles the request to insert a new {@link DictionaryValue}.
   *
   * @param dictionaryType
   *    A {@link DictionaryType} that represents the {@link Dictionary} to insert the {@link DictionaryValue} for.
   * @param dictionaryValue
   *    The {@link DictionaryValue} to insert.
   *
   * @return A JSON representation after the DictionaryValue has been inserted.
   */
  @Transactional
  @PostMapping("{dictionaryType}/values")
  public Mono<ResponseEntity<DictionaryValue>> insertDictionaryValue(@PathVariable DictionaryType dictionaryType,
                                                                     @RequestBody Mono<DictionaryValue> dictionaryValue
  ) {
    Assert.notNull(dictionaryValue, "Argument [dv] cannot be null.");

    LOGGER.debug("Received request to insertDictionaryValue for [{}]", dictionaryType);

    return dictionaryService.insertDictionaryValue(1L, dictionaryType, dictionaryValue)
        .map(result -> ResponseEntity.status(HttpStatus.CREATED).body(result));
  }

  /**
   * Handles the request to update an existing {@link DictionaryValue}.
   *
   * @param dictionaryType
   *    A {@link DictionaryType} that represents the {@link Dictionary} to update the {@link DictionaryValue} for.
   * @param id
   *    The unique identifier of the DictionaryValue to update.
   * @param dictionaryValue
   *    The DictionaryValue to update.
   *
   * @return A JSON representation after the DictionaryValue has been updated.
   */
  @Transactional
  @PutMapping("{dictionaryType}/values/{id}")
  public Mono<ResponseEntity<DictionaryValue>> updateDictionaryValue(@PathVariable DictionaryType dictionaryType,
                                                                     @PathVariable long id,
                                                                     @RequestBody Mono<DictionaryValue> dictionaryValue
  ) {
    Assert.isTrue(id > 0, "Argument [id] must be greater than 0.");
    Assert.notNull(dictionaryValue, "Argument [dv] cannot be null.");

    LOGGER.debug("Received request to updateDictionaryValue for [{}, {}]", dictionaryType, id);

    return dictionaryService.updateDictionaryValue(1L, dictionaryType, id, dictionaryValue)
        .map(ResponseEntity::ok)
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  /**
   * Handles the request to delete an existing {@link DictionaryValue}.
   *
   * @param dictionaryType
   *    A {@link DictionaryType} that represents the {@link Dictionary} to delete the {@link DictionaryValue} for.
   * @param id
   *    The unique identifier of the DictionaryValue to delete.
   */
  @Transactional
  @DeleteMapping("{dictionaryType}/values/{id}")
  public Mono<ResponseEntity<Void>> deleteDictionaryValue(@PathVariable DictionaryType dictionaryType,
                                                          @PathVariable long id
  ) {
    Assert.isTrue(id > 0, "Argument [id] must be greater than 0.");

    LOGGER.debug("Received request to deleteDictionaryValue for [{}, {}]", dictionaryType, id);

    return dictionaryService.deleteDictionaryValue(1L, dictionaryType, id)
        .map(result -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());
//        .switchIfEmpty(ResponseEntity.notFound().build());
  }
}
