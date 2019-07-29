package com.cagst.swkroa.service.dictionary;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * An implementation of the {@link DictionaryService} interface.
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
}
