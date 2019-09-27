package com.cagst.swkroa.service.person;

import java.util.Locale;

import com.cagst.swkroa.service.SwkroaBase;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.immutables.value.Value;
import org.springframework.lang.Nullable;

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
@Value.Immutable(copy = false)
@Value.Style(visibility = Value.Style.ImplementationVisibility.PACKAGE, overshadowImplementation = true)
public interface Person extends SwkroaBase {
    @JsonProperty("personId")
    @Nullable
    @Value.Auxiliary
    Long personId();

    @JsonProperty("titleCd")
    @Nullable
    Long titleCd();

    @JsonProperty("firstName")
    String firstName();

    @JsonProperty("middleName")
    @Nullable
    String middleName();

    @JsonProperty("lastName")
    @Value.Redacted
    String lastName();

    @JsonProperty("locale")
    @Value.Default
    default Locale locale() {
        return Locale.US;
    }

    // static inner Builder class extends generated or yet-to-be generated Builder
    class Builder extends ImmutablePerson.Builder {}
}
