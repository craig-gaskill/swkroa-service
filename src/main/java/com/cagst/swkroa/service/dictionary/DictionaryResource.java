package com.cagst.swkroa.service.dictionary;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Handles endpoints for retrieving and persisting {@link Dictionary} records.
 *
 * @author Craig Gaskill
 */
@RestController
@RequestMapping(value = "dictionaries")
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
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
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
  @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<ResponseEntity<Dictionary>> getDictionary(@PathVariable long id) {
    Assert.isTrue(id > 0, "Argument [id] must be greater than 0.");

    LOGGER.debug("Received request to getDictionary for [{}]", id);

    return dictionaryService.getDictionaryById(id)
        .map(ResponseEntity::ok)
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }
}
