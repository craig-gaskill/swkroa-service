package com.cagst.swkroa.service.authenticate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;

@JsonPropertyOrder({
    "username",
    "password"
})
@Value
@Accessors(fluent = true)
@Builder(toBuilder = true)
@JsonDeserialize(builder = LoginRequest.LoginRequestBuilder.class)
public class LoginRequest {
  @JsonProperty("username")
  private String username;

  @JsonProperty("password")
  private String password;
}
