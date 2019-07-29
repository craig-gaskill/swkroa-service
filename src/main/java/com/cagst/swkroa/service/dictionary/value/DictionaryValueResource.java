package com.cagst.swkroa.service.dictionary.value;

import javax.inject.Inject;

import com.cagst.swkroa.service.dictionary.Dictionary;
import com.cagst.swkroa.service.dictionary.DictionaryType;
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
 * Handles endpoints for retrieving and persisting {@link DictionaryValue} records.
 *
 * @author Craig Gaskill
 */
@RestController
@RequestMapping(value = "dictionaries/{dictionaryType}/values")
public class DictionaryValueResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(DictionaryValueResource.class);

    private final DictionaryValueService dictionaryValueService;

    @Inject
    public DictionaryValueResource(DictionaryValueService dictionaryValueService) {
        this.dictionaryValueService = dictionaryValueService;
    }

    /**
     * Handles the request and retrieves the {@link DictionaryValue} associated to the specified Dictionary.
     *
     * @param dictionaryType
     *    A {@link DictionaryType} that represents the {@link Dictionary} to retrieve {@link DictionaryValue} for.
     *
     * @return A JSON representation of the DictionaryValue associated to the specified Dictionary.
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<DictionaryValue> getDictionaryValues(@PathVariable DictionaryType dictionaryType) {
        Assert.notNull(dictionaryType, "Argument [dictionaryType] cannot be null.");

        LOGGER.debug("Received request to getDictionaryValues for [{}]", dictionaryType);

        return dictionaryValueService.getDictionaryValuesForDictionaryType(dictionaryType);
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
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<DictionaryValue>> insertDictionaryValue(@PathVariable DictionaryType dictionaryType,
                                                                       @RequestBody Mono<DictionaryValue> dictionaryValue
    ) {
        Assert.notNull(dictionaryValue, "Argument [dv] cannot be null.");

        LOGGER.debug("Received request to insertDictionaryValue for [{}]", dictionaryType);

        return dictionaryValueService.insertDictionaryValue(1L, dictionaryType, dictionaryValue)
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
    @PutMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<DictionaryValue>> updateDictionaryValue(@PathVariable DictionaryType dictionaryType,
                                                                       @PathVariable long id,
                                                                       @RequestBody Mono<DictionaryValue> dictionaryValue
    ) {
        Assert.isTrue(id > 0, "Argument [id] must be greater than 0.");
        Assert.notNull(dictionaryValue, "Argument [dv] cannot be null.");

        LOGGER.debug("Received request to updateDictionaryValue for [{}, {}]", dictionaryType, id);

        return dictionaryValueService.updateDictionaryValue(1L, dictionaryType, id, dictionaryValue)
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
    @DeleteMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Void>> deleteDictionaryValue(@PathVariable DictionaryType dictionaryType,
                                                            @PathVariable long id
    ) {
        Assert.isTrue(id > 0, "Argument [id] must be greater than 0.");

        LOGGER.debug("Received request to deleteDictionaryValue for [{}, {}]", dictionaryType, id);

        return dictionaryValueService.deleteDictionaryValue(1L, dictionaryType, id)
            .map(result -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());
//        .switchIfEmpty(ResponseEntity.notFound().build());
    }
}
