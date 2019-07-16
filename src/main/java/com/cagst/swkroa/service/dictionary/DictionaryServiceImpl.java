package com.cagst.swkroa.service.dictionary;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * An implementation of the {@link DictionaryService}
 *
 * @author Craig Gaskill
 */
@Service
/* package */ class DictionaryServiceImpl implements DictionaryService {
  private static final Logger LOGGER = LoggerFactory.getLogger(DictionaryServiceImpl.class);

  private final DictionaryRepository dictionaryRepository;

  @Inject
  public DictionaryServiceImpl(DictionaryRepository dictionaryRepository) {
    this.dictionaryRepository = dictionaryRepository;
  }

  @Override
  public Flux<Dictionary> getDictionaries() {
    LOGGER.debug("Calling getDictionaries");

    return dictionaryRepository.getDictionaries();
  }

  @Override
  public Mono<Dictionary> getDictionaryById(long id) {
    Assert.isTrue(id > 0, "Argument [id] must be greater than 0.");

    LOGGER.debug("Calling getDictionaryById for [{}]", id);

    return dictionaryRepository.getDictionaryById(id);
  }

  @Override
  public Mono<Dictionary> getDictionaryByType(DictionaryType dictionaryType) {
    Assert.notNull(dictionaryType, "Argument [dictionaryType] cannot be null.");

    LOGGER.debug("Calling getDictionaryByType for [{}]", dictionaryType);

    return dictionaryRepository.getDictionaryByType(dictionaryType);
  }

  @Override
  public Flux<DictionaryValue> getDictionaryValuesForDictionaryType(DictionaryType dictionaryType) {
    Assert.notNull(dictionaryType, "Argument [dictionaryType] cannot be null.");

    LOGGER.debug("Calling getDictionaryValuesForDictionaryType for [{}]", dictionaryType);

    return dictionaryRepository.getDictionaryValuesForDictionaryType(dictionaryType);
  }

  @Override
  public Mono<DictionaryValue> insertDictionaryValue(long userId,
                                                     DictionaryType dictionaryType,
                                                     Mono<DictionaryValue> dictionaryValue
  ) {
    Assert.isTrue(userId > 0, "Argument [userId] must be greater than 0.");
    Assert.notNull(dictionaryType, "Argument [dictionaryType] cannot be null.");
    Assert.notNull(dictionaryValue, "Argument [dictionaryValue] cannot be null.");

    LOGGER.debug("Calling insertDictionaryValue for [{}]", dictionaryType);

    return dictionaryRepository.getDictionaryByType(dictionaryType)
        .flatMap(d -> dictionaryRepository.insertDictionaryValue(userId, d.dictionaryId(), dictionaryValue));
  }

  @Override
  public Mono<DictionaryValue> updateDictionaryValue(long userId,
                                                     DictionaryType dictionaryType,
                                                     long dictionaryValueId,
                                                     Mono<DictionaryValue> dictionaryValue
  ) {
    Assert.isTrue(userId > 0, "Argument [userId] must be greater than 0.");
    Assert.notNull(dictionaryType, "Argument [dictionaryType] cannot be null.");
    Assert.isTrue(dictionaryValueId > 0, "Argument [dictionaryValueId] must be greater than 0.");
    Assert.notNull(dictionaryValue, "Argument [dictionaryValue] cannot be null.");

    LOGGER.debug("Calling updateDictionaryValue for [{}, {}]", dictionaryType, dictionaryValueId);

    return dictionaryRepository.getDictionaryValueById(dictionaryType, dictionaryValueId)
        .flatMap(dv -> dictionaryRepository.updateDictionaryValue(userId, dictionaryValue));
  }

  @Override
  public Mono<Void> deleteDictionaryValue(long userId,
                                          DictionaryType dictionaryType,
                                          long dictionaryValueId
  ) {
    Assert.notNull(dictionaryType, "Argument [dictionaryType] cannot be null.");
    Assert.isTrue(dictionaryValueId > 0, "Argument [dictionaryValueId] must be greater than 0.");

    LOGGER.debug("Received request to deleteDictionaryValue for [{}, {}]", dictionaryType, dictionaryValueId);

    return dictionaryRepository.getDictionaryValueById(dictionaryType, dictionaryValueId)
        .flatMap(dv -> {
          dv = dv.toBuilder().active(false).build();
          return dictionaryRepository.updateDictionaryValue(userId, Mono.just(dv)).then();
        });
  }
}
