package com.cagst.swkroa.service.dictionary.value;

import javax.inject.Inject;

import com.cagst.swkroa.service.dictionary.DictionaryRepository;
import com.cagst.swkroa.service.dictionary.DictionaryType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * An implementation of the {@link DictionaryValueService} interface.
 *
 * @author Craig Gaskill
 */
@Service
/* package */ class DictionaryValueServiceImpl implements DictionaryValueService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DictionaryValueServiceImpl.class);

    private final DictionaryRepository dictionaryRepository;
    private final DictionaryValueRepository dictionaryValueRepository;

    @Inject
    public DictionaryValueServiceImpl(DictionaryRepository dictionaryRepository,
                                      DictionaryValueRepository dictionaryValueRepository
    ) {
        this.dictionaryRepository = dictionaryRepository;
        this.dictionaryValueRepository = dictionaryValueRepository;
    }

    @Override
    public Flux<DictionaryValue> getDictionaryValuesForDictionaryType(DictionaryType dictionaryType) {
        Assert.notNull(dictionaryType, "Argument [dictionaryType] cannot be null.");

        LOGGER.debug("Calling getDictionaryValuesForDictionaryType for [{}]", dictionaryType);

        return dictionaryValueRepository.getDictionaryValuesForDictionaryType(dictionaryType);
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
            .flatMap(d -> dictionaryValueRepository.insertDictionaryValue(userId, d.dictionaryId(), dictionaryValue));
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

        return dictionaryValueRepository.getDictionaryValueById(dictionaryType, dictionaryValueId)
            .flatMap(dv -> dictionaryValueRepository.updateDictionaryValue(userId, dictionaryValue));
    }

    @Override
    public Mono<Void> deleteDictionaryValue(long userId,
                                            DictionaryType dictionaryType,
                                            long dictionaryValueId
    ) {
        Assert.notNull(dictionaryType, "Argument [dictionaryType] cannot be null.");
        Assert.isTrue(dictionaryValueId > 0, "Argument [dictionaryValueId] must be greater than 0.");

        LOGGER.debug("Received request to deleteDictionaryValue for [{}, {}]", dictionaryType, dictionaryValueId);

        return dictionaryValueRepository.getDictionaryValueById(dictionaryType, dictionaryValueId)
            .flatMap(dv -> {
                dv = dv.toBuilder().active(false).build();
                return dictionaryValueRepository.updateDictionaryValue(userId, Mono.just(dv)).then();
            });
    }
}
