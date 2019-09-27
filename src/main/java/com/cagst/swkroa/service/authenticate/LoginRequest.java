package com.cagst.swkroa.service.authenticate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@JsonDeserialize(builder = LoginRequest.Builder.class)
@JsonPropertyOrder({
    "username",
    "password"
})
@Value.Immutable(copy = false)
@Value.Style(visibility = Value.Style.ImplementationVisibility.PACKAGE, overshadowImplementation = true)
public interface LoginRequest {
  @JsonProperty("username")
  String username();

  @JsonProperty("password")
  String password();

  // static inner Builder class extends generated or yet-to-be generated Builder
  class Builder extends ImmutableLoginRequest.Builder {}
}
