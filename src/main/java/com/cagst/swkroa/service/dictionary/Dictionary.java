package com.cagst.swkroa.service.dictionary;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;

/**
 * Represents a Dictionary within the system.
 *
 * @author Craig Gaskill
 */
@JsonPropertyOrder({
    "dictionaryId",
    "display",
    "meaning",
    "active",
    "updateCount"
})
@Value
@Accessors(fluent = true)
@Builder(toBuilder = true)
@JsonDeserialize(builder = Dictionary.DictionaryBuilder.class)
public class Dictionary {
  @JsonProperty("dictionaryId")
  private Long dictionaryId;

  @JsonProperty("display")
  private String display;

  @JsonProperty("meaning")
  private String meaning;

  @JsonProperty("active")
  @Builder.Default
  private boolean active = true;

  @JsonProperty("updateCount")
  @Builder.Default
  private long updateCount = 0L;
}
