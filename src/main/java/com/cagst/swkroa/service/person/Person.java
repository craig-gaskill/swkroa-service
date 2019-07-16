package com.cagst.swkroa.service.person;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;

/**
 * Representation of a Person.
 *
 * @author Craig Gaskill
 */
@JsonPropertyOrder({
    "personId",
    "titleCd",
    "firstName",
    "middleName",
    "lastName",
    "locale",
    "active",
    "updateCount"
})
@Value
@Accessors(fluent = true)
@Builder(toBuilder = true)
public class Person {
    @JsonProperty("personId")
    private Long personId;

    @JsonProperty("titleCd")
    private Long titleCd;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("middleName")
    private String middleName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("locale")
    private Locale locale;

    @JsonProperty("active")
    private boolean active;

    @JsonProperty("updateCount")
    private long updateCount;
}
