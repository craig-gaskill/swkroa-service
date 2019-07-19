package com.cagst.swkroa.service.person;

import java.util.Collection;

import reactor.core.publisher.Flux;

/**
 * Definition of a repository that retrieves and persists {@link Person} objects.
 *
 * @author Craig Gaskill
 */
public interface PersonRepository {
    /**
     * Retrieves a {@link Flux} of {@link Person} that are associated with the specified
     * {@link Collection} of unique person identifiers.
     *
     * @param personIds
     *      The {@link Collection} of unique identifiers to retrieve people for.
     *
     * @return A {@link Flux} of {@link Person} that are associated with the specified set of unique ids.
     */
    Flux<Person> getPeopleByIds(Collection<Long> personIds);
}
