package com.cagst.swkroa.service.person;

import java.util.Set;

import reactor.core.publisher.Flux;

/**
 * Definition of a repository that retrieves and persists {@link Person} objects.
 *
 * @author Craig Gaskill
 */
public interface PersonRepository {
    /**
     * Retrieves a {@link Flux} of {@link Person} that are associated with the specified
     * {@link Set} of unique person identifiers.
     *
     * @param personIds
     *      The {@link Set} of unique identifiers to retrieve people for.
     *
     * @return A {@link Flux} of {@link Person} that are associated with the specified set of unique ids.
     */
    Flux<Person> getPeopleByIds(Set<Long> personIds);
}
