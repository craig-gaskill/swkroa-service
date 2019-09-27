package com.cagst.swkroa.service.dictionary.value;

import com.cagst.swkroa.service.SwkroaBase;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;
import org.springframework.lang.Nullable;

@JsonDeserialize(builder = DictionaryValue.Builder.class)
@JsonPropertyOrder({
    "dictionaryValueId",
    "display",
    "meaning",
    "active",
    "updateCount"
})
@Value.Immutable(copy = false)
@Value.Style(visibility = Value.Style.ImplementationVisibility.PACKAGE, overshadowImplementation = true)
public interface DictionaryValue extends SwkroaBase {
  @Nullable
  @Value.Auxiliary
  @JsonProperty("dictionaryValueId")
  Long dictionaryValueId();

  @JsonProperty("display")
  String display();

  @JsonProperty("meaning")
  String meaning();

  // static inner Builder class extends generated or yet-to-be generated Builder
  class Builder extends ImmutableDictionaryValue.Builder {}
}
