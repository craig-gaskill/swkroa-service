package com.cagst.swkroa.service.authenticate;

import com.cagst.swkroa.service.security.LoginStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@JsonDeserialize(builder = LoginResponse.Builder.class)
@JsonPropertyOrder({
    "status",
    "access",
    "refresh"
})
@Value.Immutable(copy = false)
@Value.Style(visibility = Value.Style.ImplementationVisibility.PACKAGE, overshadowImplementation = true)
public interface LoginResponse {
  @JsonProperty("status")
  LoginStatus loginStatus();

  @JsonProperty("access")
  String accessToken();

  @JsonProperty("refresh")
  String refreshToken();

  // static inner Builder class extends generated or yet-to-be generated Builder
  class Builder extends ImmutableLoginResponse.Builder {}
}
